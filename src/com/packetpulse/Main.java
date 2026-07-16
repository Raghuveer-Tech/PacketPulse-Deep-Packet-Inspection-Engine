package com.packetpulse;

import com.packetpulse.model.*;
import com.packetpulse.parser.PacketParser;
import com.packetpulse.parser.PcapReader;
import com.packetpulse.pipeline.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {

    private static final int NUM_LBS = 2;
    private static final int FPS_PER_LB = 2;
    private static final int NUM_FPS = NUM_LBS * FPS_PER_LB;

    public static void main(String[] args) throws InterruptedException {
        String fileName = "test_dpi.pcap";

        printBanner();
        System.out.println(" +----------------------------------------------------------------------+");
        System.out.println(" |                         INPUT FILE METADATA                          |");
        System.out.println(" +----------------------------------------------------------------------+");
        System.out.printf("   %-20s : %s\n", "File Name", fileName);
        System.out.printf("   %-20s : %.2f KB\n", "File Size", new File(fileName).length() / 1024.0);
        System.out.println(" +----------------------------------------------------------------------+\n");

        // read raw packets ---
        PcapReader reader = new PcapReader(fileName);
        List<PacketJob> packets = reader.readAllPackets();

        // parse protocol headers once, up front (fast, single-threaded) ---
        // Deep inspection (SNI + rules) happens per-thread in FastPathProcessor.
        for (PacketJob job : packets) {
            PacketParser.parse(job); // sets job.tuple / offsets; leaves job.tuple == null if unparseable
        }

        // --- Step 3: set up shared components ---
        RuleManager ruleManager = new RuleManager();
        ruleManager.blockDomain("youtube.com");
        ruleManager.blockDomain("tiktok.com");
        ruleManager.blockApp(AppType.FACEBOOK);

        DPIStats stats = new DPIStats();
        Queue<String> results = new ConcurrentLinkedQueue<>();

        //build the FastPathProcessor pool ---
        List<FastPathProcessor> fps = new ArrayList<>();
        for (int i = 0; i < NUM_FPS; i++) {
            fps.add(new FastPathProcessor(i, ruleManager, stats, results));
        }

        //build LoadBalancers, each owning a slice of FP queues ---
        List<TSQueue> fpQueues = new ArrayList<>();
        for (FastPathProcessor fp : fps) fpQueues.add(fp.getInputQueue());

        List<LoadBalancer> lbs = new ArrayList<>();
        for (int i = 0; i < NUM_LBS; i++) {
            // Each LB only gets its OWN slice of FP queues (see LoadBalancer's
            // selectFP fix — indexing must stay within the list it's given).
            List<TSQueue> ownedQueues = fpQueues.subList(i * FPS_PER_LB, i * FPS_PER_LB + FPS_PER_LB);
            lbs.add(new LoadBalancer(i, ownedQueues, i * FPS_PER_LB));
        }

        //start all workers ---
        for (FastPathProcessor fp : fps) fp.start();
        for (LoadBalancer lb : lbs) lb.start();

        //dispatch every packet into a LoadBalancer's queue ---
        for (PacketJob job : packets) {
            int lbIndex = (job.tuple != null)
                    ? Math.abs(job.tuple.hashCode()) % lbs.size()
                    : job.packetId % lbs.size();
            lbs.get(lbIndex).getInputQueue().push(job);
        }

        //wait until every dispatched packet has been processed ---
        while (stats.totalPackets.sum() < packets.size()) {
            Thread.sleep(10);
        }

        //shut everything down cleanly ---
        for (LoadBalancer lb : lbs) lb.stop();
        for (FastPathProcessor fp : fps) fp.terminate();

        //print the report ---
        printResults(results);
        printFinalReport(stats);
        printThreadStats(lbs, fps);
    }

    private static final int W_ID = 5, W_TUPLE = 45, W_DOMAIN = 22, W_APP = 12, W_ACTION = 11;

    private static void printResults(Queue<String> results) {
        List<String[]> rows = new ArrayList<>();
        for (String r : results) rows.add(r.split("\t", -1));
        rows.sort(Comparator.comparingInt(r -> Integer.parseInt(r[0])));

        String border = " +" + "-".repeat(W_ID + 2) + "+" + "-".repeat(W_TUPLE + 2) + "+"
                + "-".repeat(W_DOMAIN + 2) + "+" + "-".repeat(W_APP + 2) + "+" + "-".repeat(W_ACTION + 2) + "+";
        String rowFmt = " | %-" + W_ID + "s | %-" + W_TUPLE + "s | %-" + W_DOMAIN + "s | %-" + W_APP + "s | %-" + W_ACTION + "s |\n";

        System.out.println(" [TRAFFIC ANALYSIS LOGS]");
        System.out.println(border);
        System.out.printf(rowFmt, "ID", "FLOW TUPLE", "DOMAIN", "APP TYPE", "ACTION");
        System.out.println(border);
        for (String[] r : rows) {
            System.out.printf(rowFmt,
                    r[0],
                    truncate(r[1], W_TUPLE),
                    truncate(r[2], W_DOMAIN),
                    truncate(r[3], W_APP),
                    "[" + r[4] + "]");
        }
        System.out.println(border);
    }

    private static String truncate(String s, int width) {
        if (s == null) return "";
        if (s.length() <= width) return s;
        return s.substring(0, Math.max(0, width - 3)) + "...";
    }

    private static void printBanner() {
        System.out.println("\n==========================================================================");
        System.out.println("   PACKETPULSE : DEEP PACKET INSPECTION (DPI) ENGINE - MULTI-THREADED   ");
        System.out.println("==========================================================================\n");
    }

    private static void printFinalReport(DPIStats stats) {
        System.out.println("\n +======================================================================+");
        System.out.println(" |                    FINAL SECURITY ANALYSIS REPORT                    |");
        System.out.println(" +======================================================================+");
        System.out.printf("   %-20s : %s\n", "Generated On", LocalDateTime.now().toString().substring(0, 19));
        System.out.printf("   %-20s : %d\n", "Total Analysed", stats.totalPackets.sum());
        System.out.printf("   %-20s : %d TCP | %d UDP | %d OTHER\n", "Protocol Breakdown",
                stats.tcpPackets.sum(), stats.udpPackets.sum(), stats.otherPackets.sum());
        System.out.printf("   %-20s : %d FORWARDED | %d DROPPED\n", "Status Summary",
                stats.forwardedPackets.sum(), stats.droppedPackets.sum());
        System.out.println(" +======================================================================+\n");
    }

    private static void printThreadStats(List<LoadBalancer> lbs, List<FastPathProcessor> fps) {
        System.out.println(" +======================================================================+");
        System.out.println(" |                       THREAD STATISTICS                              |");
        System.out.println(" +======================================================================+");
        System.out.printf("   %-20s : %d Load Balancers x %d Fast Paths each = %d worker threads\n",
                "Pool Size", lbs.size(), NUM_FPS / lbs.size(), NUM_FPS);
        for (LoadBalancer lb : lbs) {
            System.out.printf("   LB%-2d dispatched      : %d packets\n", lbs.indexOf(lb), lb.packetsDispatched.sum());
        }
        for (FastPathProcessor fp : fps) {
            System.out.printf("   FP%-2d processed        : %d packets (thread: %s)\n",
                    fp.getWorkerId(), fp.packetsProcessed.sum(), fp.getName());
        }
        System.out.println(" +======================================================================+\n");
    }
}