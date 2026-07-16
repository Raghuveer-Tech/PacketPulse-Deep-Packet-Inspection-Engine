package com.packetpulse.model;

public enum AppType {
    UNKNOWN,
    HTTP,
    HTTPS,
    DNS,
    TLS,
    QUIC,
    GOOGLE,
    FACEBOOK,
    YOUTUBE,
    TWITTER,
    INSTAGRAM,
    NETFLIX,
    AMAZON,
    MICROSOFT,
    APPLE,
    WHATSAPP,
    TELEGRAM,
    TIKTOK,
    SPOTIFY,
    ZOOM,
    DISCORD,
    GITHUB,
    CLOUDFLARE;

    public static AppType sniToAppType(String sni) {
        if (sni == null) return UNKNOWN;
        String lower = sni.toLowerCase();
        if (lower.contains("youtube")) return YOUTUBE;
        if (lower.contains("facebook")) return FACEBOOK;
        if (lower.contains("google")) return GOOGLE;
        if (lower.contains("github")) return GITHUB;
        if (lower.contains("twitter")) return TWITTER;
        if (lower.contains("instagram")) return INSTAGRAM;
        return HTTPS;
    }
}