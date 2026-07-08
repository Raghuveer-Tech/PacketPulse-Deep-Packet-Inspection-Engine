package com.packetpulse.model;

import java.util.Objects;

public class FiveTuple {
    public final String srcIp;
    public final String dstIp;
    public final int srcPort;
    public final int dstPort;
    public final byte protocol; // Ye byte hai (6 = TCP, 17 = UDP)

    public FiveTuple(String srcIp, String dstIp, int srcPort, int dstPort, byte protocol) {
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.protocol = protocol;
    }

    // YEH ZAROORI HAI: Object ko String mein convert karne ke liye
    @Override
    public String toString() {
        return srcIp + ":" + srcPort + " -> " + dstIp + ":" + dstPort;
    }

    @Override
    public boolean equals(Object o) { /* ... same as your existing code ... */ return true; }
    @Override
    public int hashCode() { return Objects.hash(srcIp, dstIp, srcPort, dstPort, protocol); }
}