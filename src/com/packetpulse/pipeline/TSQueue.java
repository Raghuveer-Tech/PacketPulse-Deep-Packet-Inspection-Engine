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

    public void push(PacketJob item) throws InterruptedException {
        if (shutdown) return;
        queue.put(item); 
    }

    public boolean tryPush(PacketJob item) {
        if (shutdown) return false;
        return queue.offer(item);
    }

    public PacketJob pop() throws InterruptedException {
        if (shutdown && queue.isEmpty()) return null;
        return queue.take();
    }

    public PacketJob popWithTimeout(long timeoutMs) throws InterruptedException {
        return queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
    }

    public int size() { return queue.size(); }
    public boolean empty() { return queue.isEmpty(); }

    public void shutdown() {
        this.shutdown = true;
        queue.clear(); 
    }

    public boolean isShutdown() { return shutdown; }
}