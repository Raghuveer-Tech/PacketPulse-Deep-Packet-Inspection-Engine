package com.packetpulse.pipeline;

import com.packetpulse.model.PacketJob;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TSQueue {
    private final LinkedBlockingQueue<PacketJob> queue;
    private volatile boolean shutdown = false;

    public TSQueue(int maxSize) {
        this.queue = new LinkedBlockingQueue<>(maxSize);
    }

    // Push item to queue (blocks if full)
    public void push(PacketJob item) throws InterruptedException {
        if (shutdown) return;
        queue.put(item); 
    }

    // Try to push without blocking (Returns false if full)
    public boolean tryPush(PacketJob item) {
        if (shutdown) return false;
        return queue.offer(item);
    }

    // Pop item from queue (blocks if empty)
    public PacketJob pop() throws InterruptedException {
        if (shutdown && queue.isEmpty()) return null;
        return queue.take();
    }

    // Pop with timeout (prevents thread hanging forever)
    public PacketJob popWithTimeout(long timeoutMs) throws InterruptedException {
        return queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
    }

    public int size() { return queue.size(); }
    public boolean empty() { return queue.isEmpty(); }

    // Signal shutdown and clear structures
    public void shutdown() {
        this.shutdown = true;
        queue.clear(); // Wake up threads waiting on lock structures
    }

    public boolean isShutdown() { return shutdown; }
}