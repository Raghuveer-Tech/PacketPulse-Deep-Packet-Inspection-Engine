package com.packetpulse.parser;

import com.packetpulse.model.PacketJob;
import java.util.Optional;

public class SNIExtractor {

    // TLS Constants (C++ macro equivalents)
    private static final byte CONTENT_TYPE_HANDSHAKE = 0x16;
    private static final byte HANDSHAKE_CLIENT_HELLO = 0x01;
    private static final int EXTENSION_SNI = 0x0000;
    private static final byte SNI_TYPE_HOSTNAME = 0x00;

    // 1. Core TLS SNI Extractor Logic
    public static Optional<String> extractTLS(byte[] payload, int length) {
        try {
            if (!isTLSClientHello(payload, length)) {
                return Optional.empty();
            }

            // Record Layer parsing: Type(1B) + Version(2B) + Length(2B) = 5 Bytes skip
            int offset = 5;

            // Handshake Layer: Type(1B) + Length(3B) + Version(2B) + Random(32B)
            // Skip Type(1B) + Length(3B) + Version(2B) = 6 Bytes
            offset += 6;
            
            // Skip Random bytes (32 Bytes)
            offset += 32;

            // Skip Session ID: Read length (1 Byte) and jump forward
            int sessionIdLen = payload[offset] & 0xFF;
            offset += 1 + sessionIdLen;

            // Skip Cipher Suites: Read length (2 Bytes) and jump forward
            int cipherSuitesLen = readUint16BE(payload, offset);
            offset += 2 + cipherSuitesLen;

            // Skip Compression Methods: Read length (1 Byte) and jump forward
            int compressionLen = payload[offset] & 0xFF;
            offset += 1 + compressionLen;

            // Now we reach Extensions Length (2 Bytes)
            if (offset + 2 > length) return Optional.empty();
            int extensionsLen = readUint16BE(payload, offset);
            offset += 2;

            int extensionsEnd = offset + extensionsLen;

            // Loop through all extensions to find SNI (Type 0x0000)
            while (offset + 4 <= extensionsEnd && offset + 4 <= length) {
                int extType = readUint16BE(payload, offset);
                int extLen = readUint16BE(payload, offset + 2);
                offset += 4;

                if (extType == EXTENSION_SNI) {
                    // Inside SNI Extension: SNI List Length (2 Bytes) -> Skip
                    offset += 2; 
                    
                    // SNI Type (1 Byte) -> Must be 0x00 (hostname)
                    int sniType = payload[offset] & 0xFF;
                    offset += 1;

                    if (sniType == SNI_TYPE_HOSTNAME) {
                        int sniLen = readUint16BE(payload, offset);
                        offset += 2;

                        if (offset + sniLen <= length) {
                            // Extract byte array piece and convert to plaintext string string
                            return Optional.of(new String(payload, offset, sniLen));
                        }
                    }
                    break;
                }
                offset += extLen; // Jump to next extension block if this wasn't SNI
            }
        } catch (Exception e) {
            // Safe fallback if packet is malformed
        }
        return Optional.empty();
    }

    // 2. Helper to check if packet is actually a TLS Client Hello handshake
    public static boolean isTLSClientHello(byte[] payload, int length) {
        if (payload == null || length < 43) return false; // Min bytes required
        
        boolean isHandshake = (payload[0] & 0xFF) == CONTENT_TYPE_HANDSHAKE;
        boolean isClientHello = (payload[5] & 0xFF) == HANDSHAKE_CLIENT_HELLO;
        
        return isHandshake && isClientHello;
    }

    // 3. HTTP Host Header Extractor (For clear-text unencrypted HTTP requests)
    public static Optional<String> extractHTTP(byte[] payload, int length) {
        if (payload == null || length < 16) return Optional.empty();
        String httpData = new String(payload, 0, Math.min(length, 1024)); // Read first 1KB data
        
        if (httpData.startsWith("GET ") || httpData.startsWith("POST ")) {
            for (String line : httpData.split("\r\n")) {
                if (line.startsWith("Host: ")) {
                    return Optional.of(line.substring(6).trim());
                }
            }
        }
        return Optional.empty();
    }

    // 4. DNS Query Extractor (To track plain domain name targets)
    public static Optional<String> extractDNS(byte[] payload, int length) {
        if (payload == null || length < 12) return Optional.empty();
        return Optional.empty();
    }

    // Big Endian helper: Combines 2 bytes into single positive integer
    private static int readUint16BE(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}