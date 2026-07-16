package com.packetpulse.model;

import java.util.Objects;

public class FiveTuple {
    public final String srcIp;
    public final String dstIp;
    public final int srcPort;
    public final int dstPort;
    public final byte protocol;

    public FiveTuple(String srcIp, String dstIp, int srcPort, int dstPort, byte protocol) {
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return srcIp + ":" + srcPort + " -> " + dstIp + ":" + dstPort;
    }

    // FIX: was "return true;" before — that made every FiveTuple equal to
    // every other one, which would silently corrupt any HashMap/HashSet
    // keyed by FiveTuple (all flows would collide into one bucket).
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiveTuple)) return false;
        FiveTuple other = (FiveTuple) o;
        return srcPort == other.srcPort &&
               dstPort == other.dstPort &&
               protocol == other.protocol &&
               Objects.equals(srcIp, other.srcIp) &&
               Objects.equals(dstIp, other.dstIp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcIp, dstIp, srcPort, dstPort, protocol);
    }
}