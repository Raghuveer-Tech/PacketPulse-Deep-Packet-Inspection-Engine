package com.packetpulse.pipeline;

import com.packetpulse.model.PacketJob;
import java.util.concurrent.atomic.LongAdder;

public class FastPathProcessor extends Thread {
    private final int workerId;
    private final TSQueue inputQueue;
    private volatile boolean running = true;

    public final LongAdder packetsProcessed = new LongAdder();
    public final LongAdder packetsDropped = new LongAdder();

    public FastPathProcessor(int workerId) {
        this.workerId = workerId;
        this.inputQueue = new TSQueue(10000);
    }

    public int getWorkerId() { 
        return this.workerId;
    }

    public TSQueue getInputQueue() {
        return this.inputQueue;
    }

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
                if (job != null) {
                    packetsProcessed.increment();
                }
            } catch (InterruptedException e) {
                if (!running) break;
            }
        }
    }
}