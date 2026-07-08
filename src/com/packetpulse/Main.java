package com.packetpulse;

import com.packetpulse.model.*;
import com.packetpulse.parser.PcapReader;
import java.util.*;
import java.time.LocalDateTime;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        String fileName = "test_dpi.pcap";
        PcapReader reader = new PcapReader(fileName);
        List<PacketJob> packets = reader.readAllPackets();

        printBanner();

        // 1. INPUT METADATA (Boxed Style)
        System.out.println(" +----------------------------------------------------------------------+");
        System.out.println(" |                         INPUT FILE METADATA                          |");
        System.out.println(" +----------------------------------------------------------------------+");
        System.out.printf("   %-20s : %s\n", "File Name", fileName);
        System.out.printf("   %-20s : %.2f KB\n", "File Size", new File(fileName).length() / 1024.0);
        System.out.println(" +----------------------------------------------------------------------+\n");

        // 2. TRAFFIC ANALYSIS TABLE
        System.out.println(" [TRAFFIC ANALYSIS LOGS - REAL-TIME PROCESSING]");
        System.out.println(" +-------+---------------------------+----------------------+------------+");
        System.out.printf(" | %-5s | %-25s | %-20s | %-10s |\n", "ID", "FLOW TUPLE", "APPLICATION (SNI)", "ACTION");
        System.out.println(" +-------+---------------------------+----------------------+------------+");

        int allowed = 0, dropped = 0;
        Map<String, Integer> appStats = new HashMap<>();

        // Process all packets without any delay
        for (int i = 0; i < packets.size(); i++) {
            PacketJob p = packets.get(i);
            String domain = extractDomain(p.info);
            boolean isBlocked = PolicyEngine.isBlocked(domain);
            
            if (isBlocked) dropped++; else allowed++;
            appStats.put(domain, appStats.getOrDefault(domain, 0) + 1);

            System.out.printf(" | %-5d | %-25s | %-20s | %-10s |\n", 
                (i + 1), 
                p.tuple.toString().substring(0, Math.min(p.tuple.toString().length(), 20)) + "...", 
                domain, 
                isBlocked ? "[DROPPED]" : "[FORWARDED]");
        }
        System.out.println(" +-------+---------------------------+----------------------+------------+");

        printFinalReport(packets.size(), allowed, dropped, appStats);
    }

    public static void printBanner() {
        System.out.println("\n==========================================================================");
        System.out.println("   PACKETPULSE | ADVANCED DEEP PACKET INSPECTION (DPI) ENGINE   ");
        System.out.println("==========================================================================\n");
    }

    public static void printFinalReport(int total, int allowed, int dropped, Map<String, Integer> stats) {
        System.out.println("\n +======================================================================+");
        System.out.println(" |                    FINAL SECURITY ANALYSIS REPORT                    |");
        System.out.println(" +======================================================================+");
        System.out.printf("   %-20s : %s\n", "Generated On", LocalDateTime.now().toString().substring(0, 19));
        System.out.printf("   %-20s : %d\n", "Total Analysed", total);
        System.out.printf("   %-20s : %d FORWARDED | %d DROPPED\n", "Status Summary", allowed, dropped);
        System.out.println(" +----------------------------------------------------------------------+");
        System.out.println("   TOP APPLICATION REQUESTS:");
        stats.entrySet().stream()
             .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
             .forEach(e -> System.out.printf("   %-20s -> %d requests\n", e.getKey(), e.getValue()));
        System.out.println(" +======================================================================+\n");
    }

    public static String extractDomain(String info) {
        if (info == null || info.isEmpty()) return "Unknown";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("([a-z0-9]+(?:\\.[a-z0-9]+)+\\.[a-z]{2,})", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher m = p.matcher(info);
        return m.find() ? m.group(1).toLowerCase() : "Unknown";
    }
}