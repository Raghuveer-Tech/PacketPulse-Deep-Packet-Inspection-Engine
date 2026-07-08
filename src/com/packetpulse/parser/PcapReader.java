package com.packetpulse.parser;

import com.packetpulse.model.PacketJob;
import com.packetpulse.model.FiveTuple;
import java.io.*;
import java.util.*;

public class PcapReader {
    private final String filePath;

    public PcapReader(String filePath) { this.filePath = filePath; }

    public List<PacketJob> readAllPackets() {
        List<PacketJob> packets = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            dis.skipBytes(24);

            while (true) {
                try {
                    dis.readInt(); dis.readInt(); 
                    int len = Integer.reverseBytes(dis.readInt());
                    dis.skipBytes(4); 
                    

                    byte[] packetData = new byte[len];
                    dis.readFully(packetData);

                    if (len > 34) {
                        PacketJob job = new PacketJob();
                        job.data = packetData;
                        job.ethOffset = 0;
                        job.ipOffset = 14;
                        
                        // IP Header Length
                        int ipHeaderLen = (packetData[job.ipOffset] & 0x0F) * 4;
                        job.transportOffset = job.ipOffset + ipHeaderLen;
                        
                        // Protocol
                        byte proto = packetData[job.ipOffset + 9];
                        job.protocol = (proto == 6) ? "TCP" : (proto == 17 ? "UDP" : "OTHER");

                        // Transport Header Length
                        int transHeaderLen = (proto == 6) ? 20 : 8;
                        job.payloadOffset = job.transportOffset + transHeaderLen;
                        job.payloadLength = len - job.payloadOffset;

                        // Extraction  Payload to String (SNI)
                        if (job.payloadLength > 0) {
                            job.info = new String(packetData, job.payloadOffset, Math.min(job.payloadLength, 150));
                        } else {
                            job.info = "";
                        }

                        // Tuple
                        job.tuple = new FiveTuple(
                            String.format("%d.%d.%d.%d", packetData[18]&0xFF, packetData[19]&0xFF, packetData[20]&0xFF, packetData[21]&0xFF),
                            String.format("%d.%d.%d.%d", packetData[22]&0xFF, packetData[23]&0xFF, packetData[24]&0xFF, packetData[25]&0xFF),
                            ((packetData[job.transportOffset]&0xFF)<<8 | (packetData[job.transportOffset+1]&0xFF)),
                            ((packetData[job.transportOffset+2]&0xFF)<<8 | (packetData[job.transportOffset+3]&0xFF)),
                            proto
                        );
                        packets.add(job);
                    }
                } catch (EOFException e) { break; }
            }
        } catch (IOException e) { System.err.println("Error: " + e.getMessage()); }
        return packets;
    }
}