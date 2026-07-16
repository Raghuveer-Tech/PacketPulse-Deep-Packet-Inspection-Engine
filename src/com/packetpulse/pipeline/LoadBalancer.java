package com.packetpulse.pipeline;

import com.packetpulse.model.PacketJob;
import com.packetpulse.model.FiveTuple;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * Distributes packets to a fixed slice of FastPathProcessor queues that this
 * LoadBalancer owns, hashing on the packet's FiveTuple so that all packets
 * of the same flow always land on the same worker.
 *
 * FIX: the original selectFP() did `fpStartId + (hash % fpQueues.size())`
 * where fpQueues was the FULL list of every FP in the system. That means
 * for any LoadBalancer with fpStartId > 0, the result could exceed the
 * list's actual bounds and throw IndexOutOfBoundsException. Fixed by having
 * each LoadBalancer own only its own slice of the queue list (passed in by
 * Main via List.subList()), so it can index into it directly with no offset.
 */
public class LoadBalancer implements Runnable {
    private final int lbId;
    private final int fpStartId; // kept for logging/debugging only
    private final TSQueue inputQueue = new TSQueue(10000);
    private final List<TSQueue> fpQueues; // only the queues this LB owns

    public final LongAdder packetsReceived = new LongAdder();
    public final LongAdder packetsDispatched = new LongAdder();

    private volatile boolean running = false;
    private Thread workerThread;

    public LoadBalancer(int lbId, List<TSQueue> fpQueues, int fpStartId) {
        this.lbId = lbId;
        this.fpQueues = fpQueues;
        this.fpStartId = fpStartId;
    }

    public void start() {
        this.running = true;
        this.workerThread = new Thread(this, "LoadBalancer-Thread-" + lbId);
        this.workerThread.start();
    }

    public void stop() {
        this.running = false;
        inputQueue.shutdown();
        if (workerThread != null) workerThread.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                PacketJob job = inputQueue.pop();
                if (job == null) continue;

                packetsReceived.increment();

                int fpTargetIndex = selectFP(job.tuple);
                fpQueues.get(fpTargetIndex).push(job);

                packetsDispatched.increment();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private int selectFP(FiveTuple tuple) {
        if (tuple == null) return 0;
        int h = tuple.hashCode();
        // FIX: reusing the SAME hashCode() % same-size-modulus at two pipeline
        // stages (LB selection in Main, then FP selection here) meant the
        // second mod always reproduced the same remainder as the first —
        // collapsing dispatch onto only 2 of the 4 FPs. Re-mixing the bits
        // here (a standard integer hash finalizer) decorrelates this level
        // from whatever modulus picked this LoadBalancer in the first place.
        int mixed = (h ^ (h >>> 16)) * 0x45d9f3b;
        mixed = (mixed ^ (mixed >>> 16)) * 0x45d9f3b;
        mixed = mixed ^ (mixed >>> 16);
        return Math.abs(mixed) % fpQueues.size();
    }

    public TSQueue getInputQueue() { return inputQueue; }
}