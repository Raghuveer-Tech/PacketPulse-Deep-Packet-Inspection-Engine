package com.packetpulse.pipeline;

import com.packetpulse.model.*;
import com.packetpulse.parser.SNIExtractor;

import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.LongAdder;

/**
 * Worker thread. Pulls already-parsed PacketJobs (PacketParser has already
 * run by the time a job reaches here — see Main), does the deep-inspection
 * work: SNI extraction, rule checking, stats update, and records a result
 * line for the final report.
 */
public class FastPathProcessor extends Thread {
    private final int workerId;
    private final TSQueue inputQueue;
    private final RuleManager ruleManager;
    private final DPIStats stats;
    private final Queue<String> resultsSink; // thread-safe queue, shared across workers
    private volatile boolean running = true;

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

        String sni = null;
        if (job.payloadLength > 0 && job.payloadOffset + job.payloadLength <= job.data.length) {
            byte[] payload = Arrays.copyOfRange(job.data, job.payloadOffset, job.payloadOffset + job.payloadLength);

            if (job.tuple.dstPort == 443) {
                Optional<String> res = SNIExtractor.extractTLS(payload, payload.length);
                if (res.isPresent()) sni = res.get();
            } else if (job.tuple.dstPort == 80) {
                Optional<String> res = SNIExtractor.extractHTTP(payload, payload.length);
                if (res.isPresent()) sni = res.get();
            }
        }

        String domain = (sni != null) ? sni : "Unknown";
        AppType appType = AppType.sniToAppType(domain);

        Optional<RuleManager.BlockReason> reason =
                ruleManager.shouldBlock(job.tuple.srcIp, job.tuple.dstPort, appType, domain);

        boolean blocked = reason.isPresent();

        if (blocked) {
            stats.droppedPackets.increment();
            packetsDropped.increment();
        } else {
            stats.forwardedPackets.increment();
        }
        packetsProcessed.increment();

        String line = job.packetId + "\t" + job.tuple.toString() + "\t" + domain + "\t"
                + appType + "\t" + (blocked ? "DROPPED" : "FORWARDED");
        resultsSink.add(line);
    }
}