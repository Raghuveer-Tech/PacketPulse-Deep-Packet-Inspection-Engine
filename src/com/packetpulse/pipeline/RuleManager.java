package com.packetpulse.pipeline;

import com.packetpulse.model.AppType;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RuleManager {

    private final ReentrantReadWriteLock ipLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock appLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock domainLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock portLock = new ReentrantReadWriteLock();

    private final Set<Long> blockedIps = new HashSet<>();
    private final Set<AppType> blockedApps = new HashSet<>();
    private final Set<String> blockedDomains = new HashSet<>();
    private final Set<Integer> blockedPorts = new HashSet<>();

    public void blockIP(long ip) {
        ipLock.writeLock().lock();
        try { blockedIps.add(ip); } finally { ipLock.writeLock().unlock(); }
    }

    // NEW: convenience overload so callers can pass "192.168.1.50" directly,
    // since FiveTuple stores IPs as dotted-decimal Strings.
    public void blockIP(String dottedIp) {
        blockIP(ipToLong(dottedIp));
    }

    public boolean isIPBlocked(long ip) {
        ipLock.readLock().lock();
        try { return blockedIps.contains(ip); } finally { ipLock.readLock().unlock(); }
    }

    public boolean isIPBlocked(String dottedIp) {
        return isIPBlocked(ipToLong(dottedIp));
    }

    private static long ipToLong(String dottedIp) {
        String[] parts = dottedIp.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = (result << 8) | (Long.parseLong(part) & 0xFF);
        }
        return result;
    }

    public void blockApp(AppType app) {
        appLock.writeLock().lock();
        try { blockedApps.add(app); } finally { appLock.writeLock().unlock(); }
    }

    public boolean isAppBlocked(AppType app) {
        appLock.readLock().lock();
        try { return blockedApps.contains(app); } finally { appLock.readLock().unlock(); }
    }

    public void blockDomain(String domain) {
        domainLock.writeLock().lock();
        try { blockedDomains.add(domain.toLowerCase().trim()); } finally { domainLock.writeLock().unlock(); }
    }

    public boolean isDomainBlocked(String domain) {
        if (domain == null || domain.isEmpty()) return false;
        String lowerDomain = domain.toLowerCase().trim();

        domainLock.readLock().lock();
        try {
            if (blockedDomains.contains(lowerDomain)) return true;
            for (String blocked : blockedDomains) {
                if (lowerDomain.endsWith("." + blocked) || lowerDomain.equals(blocked)) {
                    return true;
                }
            }
            return false;
        } finally { domainLock.readLock().unlock(); }
    }

    public void blockPort(int port) {
        portLock.writeLock().lock();
        try { blockedPorts.add(port); } finally { portLock.writeLock().unlock(); }
    }

    public boolean isPortBlocked(int port) {
        portLock.readLock().lock();
        try { return blockedPorts.contains(port); } finally { portLock.readLock().unlock(); }
    }

    public static class BlockReason {
        public final String type;
        public final String detail;

        public BlockReason(String type, String detail) {
            this.type = type;
            this.detail = detail;
        }
    }

    public Optional<BlockReason> shouldBlock(String srcIp, int dstPort, AppType app, String domain) {
        if (isIPBlocked(srcIp)) {
            return Optional.of(new BlockReason("IP", "Source IP is Blacklisted"));
        }
        if (isPortBlocked(dstPort)) {
            return Optional.of(new BlockReason("PORT", "Destination Port " + dstPort + " is Blocked"));
        }
        if (isAppBlocked(app)) {
            return Optional.of(new BlockReason("APP", "Application " + app + " is Banned"));
        }
        if (isDomainBlocked(domain)) {
            return Optional.of(new BlockReason("DOMAIN", "Domain [" + domain + "] matches Block Rules"));
        }
        return Optional.empty();
    }

    public void clearAll() {
        ipLock.writeLock().lock();     try { blockedIps.clear(); }     finally { ipLock.writeLock().unlock(); }
        appLock.writeLock().lock();    try { blockedApps.clear(); }    finally { appLock.writeLock().unlock(); }
        domainLock.writeLock().lock(); try { blockedDomains.clear(); } finally { domainLock.writeLock().unlock(); }
        portLock.writeLock().lock();   try { blockedPorts.clear(); }   finally { portLock.writeLock().unlock(); }
    }
}