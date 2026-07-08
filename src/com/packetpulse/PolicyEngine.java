package com.packetpulse;

import java.util.*;

public class PolicyEngine {
    private static final Set<String> BLOCKED_DOMAINS = new HashSet<>(Arrays.asList(
        "google.com", "facebook.com", "instagram.com", "twitter.com", "tiktok.com", "github.com"
    ));

    public static boolean isBlocked(String domain) {
        if (domain == null || domain.equals("Unknown")) return false;
        
        String cleanDomain = domain.replace("www.", "");
        
        return BLOCKED_DOMAINS.contains(cleanDomain.toLowerCase());
    }
}