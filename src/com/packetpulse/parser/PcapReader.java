package com.packetpulse.parser;

import com.packetpulse.model.PacketJob;
import java.io.*;
import java.util.*;


public class PcapReader {
    private final String filePath;

    public PcapReader(String filePath) {
        this.filePath = filePath;
    }

    public List<PacketJob> readAllPackets() {
        List<PacketJob> packets = new ArrayList<>();
        int packetId = 0;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            dis.skipBytes(24); // pcap global header

            while (true) {
                try {
                    int tsSec = Integer.reverseBytes(dis.readInt());
                    int tsUsec = Integer.reverseBytes(dis.readInt());
                    int inclLen = Integer.reverseBytes(dis.readInt());
                    dis.skipBytes(4); // orig_len, unused here

                    byte[] packetData = new byte[inclLen];
                    dis.readFully(packetData);

                    if (inclLen > 34) { // minimum Eth+IP+TCP/UDP size
                        PacketJob job = new PacketJob();
                        job.packetId = ++packetId;
                        job.data = packetData;
                        job.tsSec = tsSec;
                        job.tsUsec = tsUsec;
                        packets.add(job);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading pcap file: " + e.getMessage());
        }
        return packets;
    }
}