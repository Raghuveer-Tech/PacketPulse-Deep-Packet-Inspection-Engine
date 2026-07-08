# PacketPulse - Deep Packet Inspection (DPI) Engine

## Overview

PacketPulse is a Java-based offline Deep Packet Inspection (DPI) project that demonstrates how captured network packets can be analyzed to identify network traffic and apply security policies. The application reads packets from a PCAP file, parses Ethernet, IPv4, TCP, and UDP headers, extracts useful payload information, identifies domains from network traffic, applies configurable filtering rules, and generates a detailed console-based security report.

The project is designed to help understand the internal working of packet processing, protocol parsing, application identification, and basic firewall policy enforcement using Java.

---
# Project Objective

The objective of this project is to simulate the basic workflow of a Deep Packet Inspection engine using Java. Instead of capturing live network traffic, PacketPulse processes an offline PCAP file and demonstrates how packets move through different stages of parsing, inspection, filtering, and reporting.

The project focuses on learning networking concepts, packet structures, Java object-oriented programming, modular software design, and basic network security techniques.

---
# Features
1. Read packets from PCAP files
2. Ethernet Frame Parsing
3. IPv4 Header Parsing
4. TCP Packet Parsing
5. UDP Packet Parsing
6. FiveTuple Flow Generation
7. Payload Extraction
8. TLS Client Hello Detection
9. TLS Server Name Indication (SNI) Extraction
10. Domain Name Detection
11. Rule-Based Domain Blocking
12. Console-Based Traffic Analysis
13. Security Analysis Report
14. Traffic Statistics Generation
15. Modular Java Package Structure

---


# Technologies Used

-> Programming Language
    - Java

-> Core Java Concepts
    - Object Oriented Programming
    - Exception Handling
    - Java Collections
    - Regular Expressions
    - Java Concurrency
    - File Handling

-> Collections Used
    - HashMap
    - HashSet
    - ArrayList
    - LinkedBlockingQueue

-> Networking Concepts

    - Ethernet
    - IPv4
    - TCP
    - UDP
    - TLS
    - HTTPS
    - PCAP File Structure

---

# Project Structure

```
PacketPulse-Deep-Packet-Inspection-Engine

│

├── src

│ └── com

│       └── packetpulse

│               ├── model

│               ├── parser

│               ├── pipeline

│               ├── Main.java

│               └── PolicyEngine.java

│

├── test_dpi.pcap

├── PROJECT_DETAILS.md

├── README.md

├── LICENSE

└── .gitignore
```

---
# Package Description

## model (src/com/packetpulse/model)

Contains all data models required by the application.

Files include

- AppType.java
- Connection.java
- ConnectionState.java
- DPIStats.java
- FiveTuple.java
- PacketAction.java
- PacketJob.java

---
## parser (src/com/packetpulse/parser)

Responsible for reading packets and extracting protocol information.

Files include

- PcapReader.java
- PacketParser.java
- SNIExtractor.java

---

## pipeline (src/com/packetpulse/pipeline)

Contains reusable processing components.

Files include

- TSQueue.java
- LoadBalancer.java
- FastPathProcessor.java
- RuleManager.java

---

## Main.java file

Application entry point.

Responsible for

- Reading PCAP
- Processing packets
- Displaying output
- Generating statistics

---

## PolicyEngine.java file

Contains domain blocking rules.

Current blocked domains include

- google.com
- facebook.com
- instagram.com
- github.com
- twitter.com
- tiktok.com

---

# Processing Workflow

PCAP File
    ↓
Read Packets
    ↓
Parse Ethernet Header
    ↓
Parse IPv4 Header
    ↓
Parse TCP / UDP Header
    ↓
Generate FiveTuple
    ↓
Extract Payload
    ↓
Identify Domain
    ↓
Apply Policy
    ↓
Forward / Drop
    ↓
Generate Statistics
    ↓
Display Final Report

---

# Sample Output

The application prints a structured traffic analysis report in the console.

The report contains
• Input File Metadata

Displays the PCAP filename and size before processing begins.
• Traffic Analysis Logs

Displays packet-by-packet analysis including:
- Packet ID
- Flow Tuple
- Extracted Domain
- Packet Action

Example

Packet 4

Application
www.google.com

Action
[DROPPED]

Reason
The domain exists inside the blocked domain list of the PolicyEngine.

Packet 8

Application
www.youtube.com

Action
[FORWARDED]

Reason
The domain is not present in the blocked list.

---

# Final Security Report

After processing all packets, PacketPulse generates a summary report.

The report includes

- Total Analysed Packets

- Forwarded Packets

- Dropped Packets

- Application Statistics

For the provided test dataset

Total Packets

77

Forwarded

73

Dropped

4

Blocked Domains

- google.com

- facebook.com

- instagram.com

- tiktok.com

---

# Real-World Applications

Although this project is developed for educational purposes, it demonstrates concepts used in

- Network Monitoring

- Packet Analysis

- Network Forensics

- Traffic Inspection

- Firewall Rule Processing

- Security Event Analysis

---

# Future Improvements

Future versions may include

- Live Packet Capture

- IPv6 Support

- DNS Parser

- HTTP Header Analysis

- JSON Report Export

- Configuration File Support

- GUI Dashboard

- REST API

---

# Documentation

Detailed implementation documentation is available in

PROJECT_DETAILS.md

The documentation explains

- Folder Structure

- Package Structure

- Every Java File

- Packet Flow

- Execution Flow

- Output Explanation

- Data Structures

- Design Decisions

- Real-Life Mapping

- Interview Questions

---

# Author

Raghuveer Kumawat

Java Developer

Cyber Security Enthusiast

Computer Networks Learner

---

# License

This project is licensed under the MIT License.
See the LICENSE file for complete license information.