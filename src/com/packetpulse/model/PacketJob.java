package com.packetpulse.model;

public class PacketJob {
    public int packetId;
    public FiveTuple tuple;
    public byte[] data; 
    public String protocol; 
    public String info;
    

    public int ethOffset = 0;
    public int ipOffset = 0;
    public int transportOffset = 0;
    public int payloadOffset = 0;
    public int payloadLength = 0;
    
    public byte tcpFlags = 0;
    
  
    public long tsSec;
    public long tsUsec;
}