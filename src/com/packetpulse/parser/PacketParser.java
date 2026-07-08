package com.packetpulse.parser;

import com.packetpulse.model.PacketJob;
import com.packetpulse.model.FiveTuple;

public class PacketParser {

    public static final byte FLAG_FIN = 0x01;
    public static final byte FLAG_SYN = 0x02;
    public static final byte FLAG_RST = 0x04;
    public static final byte FLAG_PSH = 0x08;
    public static final byte FLAG_ACK = 0x10;
    public static final byte FLAG_URG = 0x20;

    public static final int PROTO_ICMP = 1;
    public static final int PROTO_TCP = 6;
    public static final int PROTO_UDP = 17;

    public static final int ETHER_IPV4 = 0x0800;

    public static boolean parse(PacketJob job) {
        if (job == null || job.data == null) return false;

        int currentOffset = 0;
        int totalLen = job.data.length;

        if (totalLen < 14) return false;
        job.ethOffset = 0;

        int etherType = ((job.data[12] & 0xFF) << 8) | (job.data[13] & 0xFF);
        currentOffset += 14;

        if (etherType != ETHER_IPV4) {
            return false; 
        }

        if (totalLen < currentOffset + 20) return false;
        job.ipOffset = currentOffset;

        int versionIhl = job.data[currentOffset] & 0xFF;
        int ipVersion = (versionIhl >> 4) & 0x0F;
        int ipHeaderLen = (versionIhl & 0x0F) * 4;

        if (ipVersion != 4 || totalLen < currentOffset + ipHeaderLen) return false;

        int protocol = job.data[currentOffset + 9] & 0xFF;

        long srcIp = readUnsignedInt32(job.data, currentOffset + 12);
        long dstIp = readUnsignedInt32(job.data, currentOffset + 16);

        currentOffset += ipHeaderLen;

        int srcPort = 0;
        int dstPort = 0;
        job.transportOffset = currentOffset;

        if (protocol == PROTO_TCP) {
            if (totalLen < currentOffset + 20) return false;
            
            srcPort = ((job.data[currentOffset] & 0xFF) << 8) | (job.data[currentOffset + 1] & 0xFF);
            dstPort = ((job.data[currentOffset + 2] & 0xFF) << 8) | (job.data[currentOffset + 3] & 0xFF);
            
            job.tcpFlags = job.data[currentOffset + 13];
            int tcpDataOffset = ((job.data[currentOffset + 12] & 0xFF) >> 4) * 4;
            job.payloadOffset = currentOffset + tcpDataOffset;
            
        } else if (protocol == PROTO_UDP) {
            if (totalLen < currentOffset + 8) return false;
            
            srcPort = ((job.data[currentOffset] & 0xFF) << 8) | (job.data[currentOffset + 1] & 0xFF);
            dstPort = ((job.data[currentOffset + 2] & 0xFF) << 8) | (job.data[currentOffset + 3] & 0xFF);
            
            job.payloadOffset = currentOffset + 8;
        } else {
            return false;
        }

        if (job.payloadOffset <= totalLen) {
            job.payloadLength = totalLen - job.payloadOffset;
        } else {
            job.payloadLength = 0;
        }

        // --- FIXED HERE: Converting long type IP format to readable String Structures
        String srcIpStr = ipToString(srcIp);
        String dstIpStr = ipToString(dstIp);

        job.tuple = new FiveTuple(srcIpStr, dstIpStr, srcPort, dstPort, (byte) protocol);
        return true;
    }

    private static long readUnsignedInt32(byte[] bytes, int offset) {
        return (((long)(bytes[offset] & 0xFF) << 24) |
                ((long)(bytes[offset + 1] & 0xFF) << 16) |
                ((long)(bytes[offset + 2] & 0xFF) << 8) |
                ((long)(bytes[offset + 3] & 0xFF))) & 0xFFFFFFFFL;
    }

    public static String ipToString(long ip) {
        return String.format("%d.%d.%d.%d",
            (ip >> 24) & 0xFF, (ip >> 16) & 0xFF, (ip >> 8) & 0xFF, ip & 0xFF);
    }
}