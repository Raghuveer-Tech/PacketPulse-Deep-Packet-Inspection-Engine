package com.packetpulse.pipeline;

import com.packetpulse.model.*;
import com.packetpulse.parser.SNIExtractor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.LongAdder;

/**
 * Worker thread. Pulls already-parsed PacketJobs (PacketParser has already
 * run by the time a job reaches here — see Main), does the deep-inspection
 * work: SNI extraction, rule checking, stats update, and records a result
 * line for the final report.
 *
 * STATEFUL TRACKING: each worker keeps its own Map<FiveTuple, Connection>.
 * Because LoadBalancer + this class both hash on FiveTuple, every packet of
 * the same flow always lands on the same FastPathProcessor — so this map
 * never needs a lock even though multiple FastPathProcessors run in
 * parallel (each one only ever touches its own map, from its own thread).
 *
 * Once a flow's Connection is CLASSIFIED (we've seen its SNI and checked
 * the rules), we don't repeat SNI extraction or rule-checking for that
 * flow's later packets — we just replay the stored PacketAction. This is
 * both faster (no redundant work per packet) and more correct: a real
 * firewall blocks/allows a whole connection once it's identified, not just
 * the one packet that happened to carry the SNI.
 */
public class FastPathProcessor extends Thread {
    private final int workerId;
    private final TSQueue inputQueue;
    private final RuleManager ruleManager;
    private final DPIStats stats;
    private final Queue<String> resultsSink; // thread-safe queue, shared across workers
    private volatile boolean running = true;

    // Owned exclusively by this worker's thread — no locking needed.
    private final Map<FiveTuple, Connection> connections = new HashMap<>();

    public final LongAdder packetsProcessed = new LongAdder();
    public final LongAdder packetsDropped = new LongAdder();

    public FastPathProcessor(int workerId, RuleManager ruleManager, DPIStats stats, Queue<String> resultsSink) {
        super("FastPath-" + workerId);
        this.workerId = workerId;
        this.inputQueue = new TSQueue(10000);
        this.ruleManager = ruleManager;
        this.stats = stats;
        this.resultsSink = resultsSink;
    }

    public int getWorkerId() { return this.workerId; }
    public TSQueue getInputQueue() { return this.inputQueue; }

    public void terminate() {
        this.running = false;
        this.inputQueue.shutdown();
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                PacketJob job = inputQueue.pop();
                if (job == null) continue; // queue shut down and empty
                process(job);
            } catch (InterruptedException e) {
                if (!running) break;
            }
        }
    }

    private void process(PacketJob job) {
        stats.totalPackets.increment();
        stats.totalBytes.add(job.data.length);

        if (job.tuple == null) {
            stats.otherPackets.increment();
            return; // PacketParser couldn't parse this one (not IPv4/TCP/UDP)
        }

        if (job.tuple.protocol == 6) stats.tcpPackets.increment();
        else if (job.tuple.protocol == 17) stats.udpPackets.increment();
        else stats.otherPackets.increment();

        // --- get or create this flow's Connection ---
        Connection conn = connections.get(job.tuple);
        if (conn == null) {
            conn = new Connection();
            conn.tuple = job.tuple;
            connections.put(job.tuple, conn);
            stats.activeConnections.increment();
        }
        conn.lastSeen = System.currentTimeMillis();
        updateTcpFlags(conn, job);

        String domain;
        AppType appType;
        boolean blocked;

        if (conn.state == ConnectionState.CLASSIFIED || conn.state == ConnectionState.BLOCKED) {
            // Already know what this flow is and what to do with it —
            // skip SNI extraction and rule-checking entirely.
            domain = conn.sni.isEmpty() ? "Unknown" : conn.sni;
            appType = conn.appType;
            blocked = (conn.action == PacketAction.DROP);
        } else {
            // First time we're seeing a payload for this flow (or no SNI
            // found yet) — do the real inspection.
            String sni = extractSni(job);

            if (sni != null) {
                domain = sni;
                appType = AppType.sniToAppType(domain);

                Optional<RuleManager.BlockReason> reason =
                        ruleManager.shouldBlock(job.tuple.srcIp, job.tuple.dstPort, appType, domain);
                blocked = reason.isPresent();

                // Classify the flow once — every later packet of this
                // FiveTuple will reuse this decision.
                conn.sni = domain;
                conn.appType = appType;
                conn.action = blocked ? PacketAction.DROP : PacketAction.FORWARD;
                conn.state = blocked ? ConnectionState.BLOCKED : ConnectionState.CLASSIFIED;
            } else {
                // No SNI in this packet (e.g. handshake packet before
                // ClientHello, or non-TLS/HTTP traffic) — still check
                // IP/port rules, which don't need a domain.
                domain = "Unknown";
                appType = AppType.UNKNOWN;
                Optional<RuleManager.BlockReason> reason =
                        ruleManager.shouldBlock(job.tuple.srcIp, job.tuple.dstPort, appType, domain);
                blocked = reason.isPresent();
                // Leave conn.state as NEW/ESTABLISHED — not classified yet,
                // so the next packet on this flow will try SNI extraction again.
                if (conn.state == null) conn.state = ConnectionState.NEW;
                if (conn.state == ConnectionState.NEW) conn.state = ConnectionState.ESTABLISHED;
            }
        }

        if (blocked) {
            stats.droppedPackets.increment();
            packetsDropped.increment();
        } else {
            stats.forwardedPackets.increment();
        }
        packetsProcessed.increment();

        if (job.tuple.protocol == 6) {
            conn.packetsIn++; // simple counters; direction-awareness (in vs out
            conn.bytesIn += job.data.length; // relative to server) is a further improvement, see README
        }

        String line = job.packetId + "\t" + job.tuple.toString() + "\t" + domain + "\t"
                + appType + "\t" + (blocked ? "DROPPED" : "FORWARDED");
        resultsSink.add(line);
    }

    private String extractSni(PacketJob job) {
        if (job.payloadLength <= 0 || job.payloadOffset + job.payloadLength > job.data.length) {
            return null;
        }
        byte[] payload = Arrays.copyOfRange(job.data, job.payloadOffset, job.payloadOffset + job.payloadLength);

        if (job.tuple.dstPort == 443) {
            Optional<String> res = SNIExtractor.extractTLS(payload, payload.length);
            if (res.isPresent()) return res.get();
        } else if (job.tuple.dstPort == 80) {
            Optional<String> res = SNIExtractor.extractHTTP(payload, payload.length);
            if (res.isPresent()) return res.get();
        }
        return null;
    }

    private void updateTcpFlags(Connection conn, PacketJob job) {
        if (job.tuple.protocol != 6) return; // TCP only
        byte flags = job.tcpFlags;
        if ((flags & 0x02) != 0) conn.synSeen = true;       // SYN
        if ((flags & 0x12) == 0x12) conn.synAckSeen = true; // SYN+ACK
        if ((flags & 0x01) != 0) {                          // FIN
            conn.finSeen = true;
            conn.state = ConnectionState.CLOSED;
        }
    }
}