package com.packetpulse.parser;

import com.packetpulse.model.PacketJob;
import java.util.Optional;

public class SNIExtractor {

    private static final byte CONTENT_TYPE_HANDSHAKE = 0x16;
    private static final byte HANDSHAKE_CLIENT_HELLO = 0x01;
    private static final int EXTENSION_SNI = 0x0000;
    private static final byte SNI_TYPE_HOSTNAME = 0x00;

    public static Optional<String> extractTLS(byte[] payload, int length) {
        try {
            if (!isTLSClientHello(payload, length)) {
                return Optional.empty();
            }
            int offset = 5;
            offset += 6;
            offset += 32;

            int sessionIdLen = payload[offset] & 0xFF;
            offset += 1 + sessionIdLen;

            int cipherSuitesLen = readUint16BE(payload, offset);
            offset += 2 + cipherSuitesLen;

            int compressionLen = payload[offset] & 0xFF;
            offset += 1 + compressionLen;

            if (offset + 2 > length) return Optional.empty();
            int extensionsLen = readUint16BE(payload, offset);
            offset += 2;

            int extensionsEnd = offset + extensionsLen;

            while (offset + 4 <= extensionsEnd && offset + 4 <= length) {
                int extType = readUint16BE(payload, offset);
                int extLen = readUint16BE(payload, offset + 2);
                offset += 4;

                if (extType == EXTENSION_SNI) {
                   
                    offset += 2; 
            
                    int sniType = payload[offset] & 0xFF;
                    offset += 1;

                    if (sniType == SNI_TYPE_HOSTNAME) {
                        int sniLen = readUint16BE(payload, offset);
                        offset += 2;

                        if (offset + sniLen <= length) {
                            return Optional.of(new String(payload, offset, sniLen));
                        }
                    }
                    break;
                }
                offset += extLen;
            }
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    public static boolean isTLSClientHello(byte[] payload, int length) {
        if (payload == null || length < 43) return false; // Min bytes required
        
        boolean isHandshake = (payload[0] & 0xFF) == CONTENT_TYPE_HANDSHAKE;
        boolean isClientHello = (payload[5] & 0xFF) == HANDSHAKE_CLIENT_HELLO;
        
        return isHandshake && isClientHello;
    }

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

    public static Optional<String> extractDNS(byte[] payload, int length) {
        if (payload == null || length < 12) return Optional.empty();
        return Optional.empty();
    }

    private static int readUint16BE(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}