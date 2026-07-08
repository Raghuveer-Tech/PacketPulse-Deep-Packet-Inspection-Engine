package com.packetpulse.pipeline;

import com.packetpulse.model.PacketJob;
import com.packetpulse.model.FiveTuple;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class LoadBalancer implements Runnable {
    private final int lbId;
    private final int fpStartId;
    private final TSQueue inputQueue = new TSQueue(10000);
    private final List<TSQueue> fpQueues;
    
    // Statistics Counters (Thread-Safe)
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
                
                // Route safely using FiveTuple consistent hash math
                int fpTargetIndex = selectFP(job.tuple);
                fpQueues.get(fpTargetIndex).push(job);
                
                packetsDispatched.increment();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Clean pipeline breakout
            }
        }
    }

    private int selectFP(FiveTuple tuple) {
        if (tuple == null) return 0;
        // Formula: Math.abs ensures positive index calculation bounds
        int hash = Math.abs(tuple.hashCode());
        return fpStartId + (hash % fpQueues.size());
    }

    public TSQueue getInputQueue() { return inputQueue; }
}

// ============================================================================
// LBManager - Groups and launches Multiple Load Balancing Threads
// ============================================================================
class LBManager {
    private final List<LoadBalancer> lbs = new ArrayList<>();

    public LBManager(int numLbs, int fpsPerLb, List<TSQueue> fpQueues) {
        for (int i = 0; i < numLbs; i++) {
            lbs.add(new LoadBalancer(i, fpQueues, i * fpsPerLb));
        }
    }

    public void startAll() {
        for (LoadBalancer lb : lbs) lb.start();
    }

    public void stopAll() {
        for (LoadBalancer lb : lbs) lb.stop();
    }

    public LoadBalancer getLBForPacket(FiveTuple tuple) {
        int index = Math.abs(tuple.hashCode()) % lbs.size();
        return lbs.get(index);
    }
}