package com.packetpulse.model;

import java.util.concurrent.atomic.LongAdder;

public class DPIStats {
    public final LongAdder totalPackets = new LongAdder();
    public final LongAdder totalBytes = new LongAdder();
    public final LongAdder forwardedPackets = new LongAdder();
    public final LongAdder droppedPackets = new LongAdder();
    public final LongAdder tcpPackets = new LongAdder();
    public final LongAdder udpPackets = new LongAdder();
    public final LongAdder otherPackets = new LongAdder();
    public final LongAdder activeConnections = new LongAdder();
}