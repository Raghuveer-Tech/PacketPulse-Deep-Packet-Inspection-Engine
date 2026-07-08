# PROJECT DETAILS

# PacketPulse - Deep Packet Inspection (DPI) Engine

Author: Raghuveer Kumawat

Language: Java

Project Type: Network Security | Deep Packet Inspection (DPI)

Version: 1.0

---

# Table of Contents

1. Introduction
2. Project Overview
3. Project Objective
4. Why PacketPulse?
5. What is Deep Packet Inspection (DPI)?
6. Problem Statement
7. Solution
8. Technologies Used
9. Networking Concepts Used
10. Software Requirements
11. Hardware Requirements
12. Project Folder Structure
13. Package Structure
14. Input
15. Output
16. Setup
17. Compilation
18. Execution

---

# 1. Introduction

PacketPulse is a Java-based Deep Packet Inspection (DPI) Engine developed to demonstrate how captured network packets can be inspected, analyzed, and filtered using rule-based policies.

Instead of capturing live network traffic, the application works with an offline PCAP (Packet Capture) file. Each packet stored inside the PCAP file is processed one by one to understand its protocol information, payload content, and destination domain.

The project demonstrates the internal workflow of packet parsing, packet inspection, protocol analysis, payload processing, and security policy enforcement using Java.

PacketPulse is designed primarily as a learning project for students, Java developers, networking enthusiasts, and cyber security beginners who want to understand how packet inspection works inside modern network security devices.

---

# 2. Project Overview

PacketPulse processes an offline PCAP file and performs multiple stages of packet analysis.

The application starts by reading packets from the PCAP file. Each packet is parsed to extract Ethernet, IPv4, TCP, or UDP header information. The parser then extracts payload data, which may contain useful information such as domain names.

After extracting the required information, the Policy Engine compares the detected domain against predefined security rules. If the domain belongs to the blocked list, the packet is marked as DROPPED. Otherwise, it is marked as FORWARDED.

Finally, the application generates a detailed console-based security analysis report that summarizes the processed traffic.

---

# 3. Project Objective

The primary objective of PacketPulse is to demonstrate the complete workflow of Deep Packet Inspection using Java.

The project focuses on:

• Understanding packet structures

• Reading offline PCAP files

• Parsing Ethernet frames

• Parsing IPv4 packets

• Parsing TCP and UDP packets

• Extracting packet payload

• Identifying destination domains

• Applying rule-based packet filtering

• Generating traffic statistics

• Producing a structured security report

Rather than building a production firewall, PacketPulse is intended to provide a practical understanding of how packet inspection systems operate internally.

---

# 4. Why PacketPulse?

Modern computer networks exchange millions of packets every second.

Basic packet filtering techniques only inspect source IP addresses, destination IP addresses, protocol numbers, and port numbers.

However, modern security systems often need to inspect packet payloads to understand which application or domain is actually being accessed.

PacketPulse demonstrates this concept by reading packet payloads and extracting domain information before making security decisions.

Although the project is simplified for educational purposes, it introduces the same packet processing concepts used in enterprise packet inspection systems.

---

# 5. What is Deep Packet Inspection (DPI)?

Deep Packet Inspection (DPI) is a network traffic inspection technique that examines both packet headers and packet payloads.

Unlike traditional packet filtering, DPI analyzes the actual content of packets to identify applications, websites, or protocols.

Typical DPI systems perform tasks such as:

• Protocol identification

• Application detection

• Malware inspection

• Intrusion detection

• Firewall rule processing

• Traffic classification

PacketPulse demonstrates a simplified version of this workflow using Java.

---

# 6. Problem Statement

When only packet headers are inspected, it is often impossible to determine which website or application generated the traffic.

For example:

Destination Port: 443

This only indicates HTTPS traffic.

It does not reveal whether the user accessed:

• Google

• Facebook

• YouTube

• GitHub

• Netflix

To identify the actual destination, packet payload inspection becomes necessary.

PacketPulse demonstrates how packet payloads can be analyzed to identify destination domains.

---

# 7. Solution

PacketPulse solves this problem using a multi-stage processing pipeline.

The application:

Reads captured packets from a PCAP file.

↓

Parses Ethernet headers.

↓

Parses IPv4 headers.

↓

Parses TCP or UDP headers.

↓

Extracts packet payload.

↓

Searches payload for domain information.

↓

Compares detected domain with Policy Engine rules.

↓

Marks packet as FORWARDED or DROPPED.

↓

Generates traffic statistics.

↓

Displays the final security analysis report.

---

# 8. Technologies Used

Programming Language

• Java

Core Java Concepts

• Object-Oriented Programming (OOP)

• Exception Handling

• File Handling

• Java Collections Framework

• Java Concurrency

Collections Used

• HashMap

• HashSet

• ArrayList

• LinkedBlockingQueue

• LongAdder

Development Tools

• Visual Studio Code

• Git

• GitHub

---

# 9. Networking Concepts Used

PacketPulse implements several networking concepts.

These include:

• Ethernet Frame Parsing

• IPv4 Packet Parsing

• TCP Header Parsing

• UDP Header Parsing

• FiveTuple Flow Generation

• TLS Client Hello Detection

• Server Name Indication (SNI)

• Payload Extraction

• Rule-Based Packet Filtering

• Traffic Statistics

These concepts form the foundation of modern packet inspection systems.

---

# 10. Software Requirements

To run PacketPulse successfully, the following software should be installed.

• Java JDK 17 or later

• Visual Studio Code (or any Java IDE)

• Git (Optional)

Supported Operating Systems

• Windows

• Linux

• macOS

---

# 11. Hardware Requirements

Minimum Requirements

RAM

4 GB

Processor

Intel Core i3 (or equivalent)

Storage

100 MB free disk space

Recommended

8 GB RAM

Intel Core i5 or higher

SSD Storage

---

# 12. Project Folder Structure

PacketPulse-Deep-Packet-Inspection-Engine

│

├── docs/

│ ├── input/

│ └── output/

│

├── src/

│ └── com/

│ └── packetpulse/

│ ├── model/

│ ├── parser/

│ ├── pipeline/

│ ├── Main.java

│ └── PolicyEngine.java

│

├── test_dpi.pcap

├── README.md

├── PROJECT_DETAILS.md

├── LICENSE

└── .gitignore

The project follows a modular package structure to keep networking logic, data models, parsing logic, and processing pipeline separated.

---

# 13. Package Structure

The source code is divided into multiple packages.

model

Contains all shared data models used throughout the project.

parser

Responsible for reading packets and parsing protocol headers.

pipeline

Contains packet processing components including queues, rule management, and worker threads.

Main.java

Application entry point.

PolicyEngine.java

Contains predefined domain filtering rules.

This modular design improves readability, maintainability, and future scalability.

---

# 14. Project Input

PacketPulse accepts an offline packet capture file named:

test_dpi.pcap

A PCAP (Packet Capture) file stores real network packets captured from a network interface.

Each packet contains protocol headers and payload data.

During execution, PacketPulse reads every packet sequentially and sends it through the packet processing pipeline.

Input Screenshot

<p align="center">
<img src="docs/input/Input-1.png" width="900">
</p>

<p align="center">
<img src="docs/input/Input-2.png" width="900">
</p>

<p align="center">
<img src="docs/input/Input-3.png" width="900">
</p>
---

# 15. Project Output

After processing all packets, PacketPulse generates a structured console report.

The generated report contains:

• Input File Metadata

• Packet Analysis Logs

• Flow Tuple

• Detected Domain

• Packet Action

• Final Security Analysis Report

• Traffic Statistics

Output Screenshots

<p align="center">
<img src="docs/output/Output-1.png" width="900">
</p>

<p align="center">
<img src="docs/output/Output-2.png" width="900">
</p>

<p align="center">
<img src="docs/output/Output-3.png" width="900">
</p>

<p align="center">
<img src="docs/output/Output-4.png" width="900">
</p>

---

# 16. Project Setup

Clone the repository.

Open the project in Visual Studio Code or any Java IDE.

Ensure that the file

test_dpi.pcap

is present in the project root directory.

Open the integrated terminal.

Compile the Java source files.

Run the application.

---

# 17. Compile the Project

Run the following command from the project root directory.

```bash
javac -d bin src/com/packetpulse/model/*.java src/com/packetpulse/parser/*.java src/com/packetpulse/pipeline/*.java src/com/packetpulse/*.java
```

The above command compiles all Java source files and stores generated `.class` files inside the `bin` directory.

---

# 18. Run the Project

Execute the following command.

```bash
java -cp bin com.packetpulse.Main
```

After execution, PacketPulse processes every packet from `test_dpi.pcap`, applies packet inspection, evaluates the security policy, and displays the final traffic analysis report in the terminal.

---
# 19. Source Code Architecture

The source code follows a modular architecture where each package is responsible for a specific task. Instead of writing all functionality inside a single file, the project separates data models, packet parsing logic, packet processing components, and application logic into independent packages.

The overall source code structure is shown below.

src
│
└── com
    │
    └── packetpulse
        │
        ├── model
        ├── parser
        ├── pipeline
        ├── Main.java
        └── PolicyEngine.java

This modular design improves code readability, maintainability, and future scalability.

---

# 20. model Package

Location

src/com/packetpulse/model

Purpose

The model package contains all shared data structures used throughout the project.

Instead of passing raw variables between classes, PacketPulse stores packet information inside model objects.

Files inside model package

AppType.java

Connection.java

ConnectionState.java

DPIStats.java

FiveTuple.java

PacketAction.java

PacketJob.java

---

# 21. AppType.java

Purpose

AppType represents the application detected from the inspected packet.

Instead of working with plain strings, the project uses an Enumeration (Enum).

Applications currently included

UNKNOWN

HTTP

HTTPS

DNS

TLS

GOOGLE

FACEBOOK

YOUTUBE

TWITTER

INSTAGRAM

NETFLIX

AMAZON

MICROSOFT

APPLE

WHATSAPP

TELEGRAM

TIKTOK

SPOTIFY

ZOOM

DISCORD

GITHUB

CLOUDFLARE

How it works

When the SNI or domain name is extracted from the packet payload, the application checks whether the domain belongs to a known application.

Example

www.google.com

↓

GOOGLE

www.youtube.com

↓

YOUTUBE

If no application matches, UNKNOWN is returned.

---

# 22. Connection.java

Purpose

Connection.java stores information related to a network connection.

Each connection contains

FiveTuple

Connection State

Detected Application

SNI

Packet Counters

Byte Counters

Packet Action

TCP Flag Status

Connection Time

It acts as a central object that keeps all information related to one communication session.

---

# 23. ConnectionState.java

Purpose

Represents the current state of a connection.

Possible states

NEW

ESTABLISHED

CLASSIFIED

BLOCKED

CLOSED

These values help represent the lifecycle of a packet flow.

---

# 24. DPIStats.java

Purpose

Stores traffic statistics generated during packet processing.

Statistics include

Total Packets

Total Bytes

Forwarded Packets

Dropped Packets

TCP Packets

UDP Packets

Other Packets

Active Connections

Why LongAdder?

The project uses LongAdder because it provides better performance than AtomicLong during concurrent updates.

---

# 25. FiveTuple.java

Purpose

FiveTuple uniquely identifies a network flow.

A FiveTuple consists of

Source IP

Destination IP

Source Port

Destination Port

Protocol

Example

192.168.1.10:54552

↓

142.250.185.206:443

↓

TCP

FiveTuple is widely used in firewalls, routers, and packet inspection systems to identify network sessions.

---

# 26. PacketAction.java

Purpose

Represents the action that should be taken after packet inspection.

Possible actions

FORWARD

DROP

INSPECT

LOG_ONLY

Current Implementation

PacketPulse mainly uses

FORWARD

DROP

These actions are displayed inside the final traffic analysis report.

---

# 27. PacketJob.java

Purpose

PacketJob represents a single packet.

Every packet loaded from the PCAP file is stored inside a PacketJob object.

Stored information includes

Raw Packet Bytes

Packet ID

FiveTuple

Protocol

Payload

Header Offsets

Payload Offset

Payload Length

TCP Flags

Timestamp

This object travels through the packet processing pipeline.

---

# 28. parser Package

Location

src/com/packetpulse/parser

Purpose

Responsible for reading packet data and extracting protocol information.

Files

PcapReader.java

PacketParser.java

SNIExtractor.java

---

# 29. PcapReader.java

Purpose

Reads packets from an offline PCAP file.

Responsibilities

Open PCAP file

Read Global Header

Read Packet Header

Read Packet Data

Create PacketJob

Store Packet Information

Return packet list

Output

List<PacketJob>

This class acts as the starting point of packet processing.

---

# 30. PacketParser.java

Purpose

Parses raw packet bytes.

Protocols Parsed

Ethernet

IPv4

TCP

UDP

Operations Performed

Read Ethernet Header

Read IPv4 Header

Extract Source IP

Extract Destination IP

Extract Source Port

Extract Destination Port

Read TCP Flags

Calculate Payload Offset

Calculate Payload Length

Generate FiveTuple

PacketParser converts raw binary packets into meaningful protocol information.

---

# 31. SNIExtractor.java

Purpose

Extracts useful information from packet payload.

Current Functions

TLS Client Hello Detection

TLS Server Name Indication Extraction

HTTP Host Header Extraction

Domain Detection

How it works

Payload

↓

TLS Detection

↓

Read Extensions

↓

Extract SNI

↓

Return Domain Name

Example

www.google.com

www.youtube.com

www.facebook.com

If SNI cannot be extracted, an empty result is returned.

---

# 32. pipeline Package

Location

src/com/packetpulse/pipeline

Purpose

Contains packet processing utilities.

Files

TSQueue.java

LoadBalancer.java

FastPathProcessor.java

RuleManager.java

---

# 33. TSQueue.java

Purpose

Thread-safe queue implementation.

Internally uses

LinkedBlockingQueue

Responsibilities

Store PacketJob

Push Packets

Pop Packets

Queue Shutdown

Blocking Operations

The queue allows worker threads to safely exchange packets.

---

# 34. FastPathProcessor.java

Purpose

Represents a worker thread.

Responsibilities

Receive packets

Read packets from TSQueue

Process packets

Maintain processing statistics

Each FastPathProcessor has

Worker ID

Input Queue

Packet Counter

Drop Counter

Although the current Main class processes packets sequentially, this component demonstrates how packet processing can be distributed across multiple worker threads.

---

# 35. LoadBalancer.java

Purpose

Distributes packets among worker queues.

Responsibilities

Receive packets

Calculate hash

Select worker

Forward packet

PacketPulse uses FiveTuple hashing to ensure that packets belonging to the same network flow are consistently routed to the same processing queue.

---

# 36. RuleManager.java

Purpose

Stores packet filtering rules.

Supported Rules

Blocked IP

Blocked Port

Blocked Domain

Blocked Application

RuleManager also provides helper methods for checking whether a packet should be blocked based on configured rules.

Although the current project mainly uses PolicyEngine for domain filtering, RuleManager demonstrates how a modular rule management system can be implemented.

---

# 37. Main.java

Location

src/com/packetpulse/Main.java

Purpose

Main.java is the entry point of the PacketPulse application.

When the application starts, execution begins from the main() method.

Main.java coordinates the complete packet inspection workflow by calling different classes responsible for packet reading, packet processing, policy checking, and report generation.

Main Responsibilities

• Load the PCAP file

• Read all packets

• Display input metadata

• Process packets one by one

• Extract domain names

• Apply security policy

• Decide whether packets should be forwarded or dropped

• Generate traffic statistics

• Display the final security report

Execution Sequence

Application Starts

↓

Read test_dpi.pcap

↓

Load all packets

↓

Display File Information

↓

Process Every Packet

↓

Extract Domain

↓

Apply Policy

↓

Forward / Drop Packet

↓

Update Statistics

↓

Generate Final Report

---

# 38. PolicyEngine.java

Location

src/com/packetpulse/PolicyEngine.java

Purpose

PolicyEngine is responsible for making security decisions based on detected domain names.

Instead of inspecting protocols, this class simply checks whether the extracted domain belongs to the blocked domain list.

Current Blocked Domains

google.com

facebook.com

instagram.com

github.com

twitter.com

tiktok.com

Working Process

Detected Domain

↓

Normalize Domain Name

↓

Search Block List

↓

Found?

↓

YES → DROP Packet

NO → FORWARD Packet

Example

Detected Domain

www.google.com

↓

Normalized

google.com

↓

Blocked

↓

Packet Action

DROPPED

Another Example

Detected Domain

www.youtube.com

↓

Normalized

youtube.com

↓

Not Present in Block List

↓

Packet Action

FORWARDED

---

# 39. Complete Packet Processing Flow

PacketPulse follows a sequential packet inspection pipeline.

The complete processing flow is shown below.

test_dpi.pcap

↓

PcapReader

↓

PacketJob Objects

↓

PacketParser

↓

Ethernet Parsing

↓

IPv4 Parsing

↓

TCP / UDP Parsing

↓

FiveTuple Generation

↓

Payload Extraction

↓

Domain Detection

↓

PolicyEngine

↓

Packet Decision

↓

Traffic Statistics

↓

Console Report

Each stage performs a dedicated task before passing the packet to the next stage.

This modular approach keeps the implementation simple and maintainable.

---

# 40. Packet Lifecycle

Every packet inside the PCAP file passes through multiple processing stages.

Stage 1

Packet is read from PCAP.

↓

Stage 2

Packet headers are parsed.

↓

Stage 3

Protocol information is extracted.

↓

Stage 4

Payload offset is calculated.

↓

Stage 5

Payload data is extracted.

↓

Stage 6

Domain information is detected.

↓

Stage 7

Policy Engine checks security rules.

↓

Stage 8

Packet is marked as

FORWARDED

or

DROPPED

↓

Stage 9

Traffic statistics are updated.

↓

Stage 10

Information is displayed inside the final report.

---

# 41. Input Explanation

The application accepts a PCAP file named

test_dpi.pcap

as input.

A PCAP file stores packets captured from a real network.

Each packet contains

Ethernet Header

↓

IP Header

↓

TCP / UDP Header

↓

Payload

PacketPulse reads every packet stored inside the file and converts it into Java objects for processing.

Input Screenshots

(Add Images)

docs/input/Input-1.png

Figure 1

Sample packets captured using Wireshark.

docs/input/Input-2.png

Figure 2

Network packet details before processing.

docs/input/Input-3.png

Figure 3

Captured HTTPS traffic used as project input.

---

# 42. Output Explanation

After all packets have been processed, PacketPulse generates a structured console report.

The generated report consists of three major sections.

Section 1

Input File Metadata

Displays

File Name

File Size

This confirms which PCAP file is currently being processed.

Example

File Name

test_dpi.pcap

File Size

6.83 KB

---

Section 2

Traffic Analysis Logs

Displays packet-by-packet inspection results.

Each row contains

Packet ID

↓

Flow Tuple

↓

Detected Domain

↓

Packet Action

Example

Packet

4

Domain

www.google.com

Action

[DROPPED]

Reason

The detected domain exists in the blocked domain list maintained by PolicyEngine.

Another Example

Packet

8

Domain

www.youtube.com

Action

[FORWARDED]

Reason

The domain is not blocked.

---

Section 3

Final Security Analysis Report

After processing all packets, the application generates a summary report.

The report displays

Generated Time

↓

Total Packets

↓

Forwarded Packets

↓

Dropped Packets

↓

Top Detected Domains

↓

Traffic Statistics

For the supplied dataset

Total Packets

77

Forwarded

73

Dropped

4

Top Domains

Unknown

www.google.com

www.facebook.com

www.youtube.com

www.instagram.com

www.amazon.com

www.netflix.com

www.apple.com

www.cloudflare.com

www.microsoft.com

open.spotify.com

web.telegram.org

www.tiktok.com

Output Screenshots

(Add Images)

docs/output/Output-1.png

Figure 1

Application startup and input metadata.

docs/output/Output-2.png

Figure 2

Packet-by-packet traffic inspection logs.

docs/output/Output-3.png

Figure 3

Continuation of traffic analysis.

docs/output/Output-4.png

Figure 4

Final Security Analysis Report.

---

# 43. Data Flow

The movement of data inside PacketPulse is illustrated below.

PCAP File

↓

Read Packet

↓

Create PacketJob

↓

Parse Packet

↓

Generate FiveTuple

↓

Extract Payload

↓

Detect Domain

↓

Apply Policy

↓

Generate Statistics

↓

Display Report

No packet skips any processing stage.

Each packet follows the same inspection sequence.

---

# 44. Console Report Interpretation

The console output generated by PacketPulse is designed to be human-readable.

The report allows users to quickly identify

Which packets were processed

↓

Which domains were detected

↓

Which packets were blocked

↓

Overall traffic statistics

This makes the application useful for demonstrating packet inspection workflows during presentations and interviews.

---

# 45. Real-Life Mapping

Although PacketPulse is an educational implementation, the concepts demonstrated are widely used in networking and cyber security.

Similar techniques are used in

Enterprise Firewalls

↓

Network Monitoring Systems

↓

Traffic Analysis Tools

↓

Network Forensics

↓

Cyber Security Research

↓

Protocol Analysis

↓

Security Demonstrations

PacketPulse provides a simplified implementation of these concepts using Java.

---

# 46. Design Decisions

During the development of PacketPulse, the project was designed using a modular architecture instead of placing all logic inside a single Java file.

Each package has a specific responsibility.

• model package stores shared data objects.

• parser package is responsible for reading and parsing packets.

• pipeline package contains reusable packet processing components.

• PolicyEngine manages security rules.

• Main.java controls the overall execution flow.

This separation of responsibilities makes the project easier to understand, maintain, and extend.

---

# 47. Object-Oriented Programming (OOP)

PacketPulse is implemented using Object-Oriented Programming principles.

The project uses multiple classes to separate different functionalities.

OOP concepts used include:

### Encapsulation

Packet information is stored inside dedicated classes such as:

• PacketJob

• FiveTuple

• Connection

instead of using scattered variables.

---

### Abstraction

Each class performs only one primary task.

Examples

PcapReader

↓

Reads packets only.

PacketParser

↓

Parses packet headers only.

PolicyEngine

↓

Applies security rules only.

Main

↓

Coordinates the complete application.

---

### Modularity

The project is divided into independent packages.

This makes future modifications easier without affecting unrelated components.

---

# 48. Data Structures Used

PacketPulse uses different Java data structures depending on the requirement.

### HashMap

Used for

Traffic statistics

Application counters

Reason

Provides fast key-value lookup.

---

### HashSet

Used for

Blocked domains

Blocked applications

Blocked ports

Reason

Fast lookup with average O(1) search complexity.

---

### ArrayList

Used for

Storing packets loaded from the PCAP file.

Reason

Easy sequential traversal.

---

### LinkedBlockingQueue

Used inside TSQueue.

Reason

Provides thread-safe queue operations for producer-consumer style packet processing.

---

### LongAdder

Used inside DPIStats.

Reason

Efficient counter updates when multiple threads update statistics.

---

# 49. Why FiveTuple?

A FiveTuple uniquely identifies one network flow.

It consists of

Source IP

Destination IP

Source Port

Destination Port

Protocol

Instead of identifying packets individually, modern networking devices identify complete communication sessions using FiveTuple.

PacketPulse also generates FiveTuple information for every parsed packet.

---

# 50. Why PCAP Files?

Instead of capturing live traffic, PacketPulse processes offline PCAP files.

Advantages

• Easy to test

• Repeatable results

• No administrator permissions required

• Safe learning environment

• Suitable for academic demonstrations

This approach allows packet inspection without requiring live network access.

---

# 51. Why Rule-Based Filtering?

The project uses a simple rule-based PolicyEngine.

The detected domain is compared against a predefined blocked domain list.

If found,

Packet → DROPPED

Otherwise,

Packet → FORWARDED

This demonstrates the basic idea of policy enforcement used in firewalls.

---

# 52. Current Limitations

The current implementation intentionally focuses on demonstrating the core concepts of offline packet inspection.

Current limitations include:

• Offline PCAP processing only

• IPv4 packets only

• No live packet capture

• Limited HTTP parsing

• DNS parsing is not fully implemented

• Domain filtering uses predefined rules

• Console-based output only

These limitations are expected because the project is educational rather than a production-ready DPI system.

---

# 53. Future Improvements

The following features can be added in future versions.

• Live packet capture

• IPv6 packet parsing

• Complete DNS parser

• HTTP header inspection

• Configuration file for security rules

• JSON report export

• Web dashboard

• GUI interface

• Database integration

• REST API

These features are **not implemented** in the current version.

---

# 54. Error Handling

PacketPulse performs basic validation while processing packets.

Examples

• Invalid packet length

• Unsupported protocol

• Corrupted packet

• Empty payload

• Missing data

Malformed packets are safely ignored without terminating the application.

---

# 55. Performance Considerations

The project uses efficient Java collections for packet processing.

Examples

HashSet

↓

Fast blocked-domain lookup.

HashMap

↓

Fast statistics update.

LinkedBlockingQueue

↓

Thread-safe packet queue.

LongAdder

↓

Efficient concurrent counters.

Although the current execution is sequential through Main.java, reusable multithreading components are already included inside the pipeline package.

---

# 56. Security Considerations

PacketPulse performs inspection on captured packet data only.

The application

• Does not modify packets.

• Does not inject network traffic.

• Does not capture live traffic.

• Does not communicate over the network.

The project is intended only for packet analysis and educational purposes.

---

# 57. Skills Demonstrated

This project demonstrates practical knowledge of:

Programming

• Java

• Object-Oriented Programming

• Exception Handling

Networking

• Ethernet

• IPv4

• TCP

• UDP

• TLS

• Packet Parsing

• FiveTuple

Security

• Deep Packet Inspection (DPI)

• Rule-Based Filtering

• Traffic Inspection

• Domain Identification

Java Collections

• HashMap

• HashSet

• ArrayList

• LinkedBlockingQueue

• LongAdder

Development

• Git

• GitHub

• VS Code

• Modular Software Design

---

# 58. Interview Preparation

This project can be used to explain the following topics during technical interviews.

Java

• OOP

• Collections

• Multithreading

• Exception Handling

Networking

• Ethernet Frame

• IPv4

• TCP

• UDP

• TLS

• Packet Flow

Cyber Security

• Deep Packet Inspection

• Packet Parsing

• Rule-Based Firewall

• Traffic Analysis

Software Engineering

• Modular Architecture

• Package Design

• Data Structures

• Clean Code Organization

---

# 59. Conclusion

PacketPulse demonstrates how offline network packets can be parsed, inspected, and analyzed using Java.

The project reads packets from a PCAP file, extracts protocol information, identifies destination domains, applies rule-based filtering, and generates a structured traffic analysis report.

Although simplified for educational purposes, the project provides practical exposure to packet parsing, networking concepts, Java programming, and basic Deep Packet Inspection techniques.

The modular architecture, clear package structure, and detailed documentation make PacketPulse suitable as a learning project, academic project, and portfolio project for demonstrating Java and Computer Networks fundamentals.

---

# End of Documentation

Thank you for reading the PacketPulse project documentation.

Author

**Raghuveer Kumawat**

Software Engineer | Computer Science & Engineering | Full Stack Developer






// For Interview 
# 60. Frequently Asked Interview Questions

This section contains some commonly asked interview questions based on the implementation of PacketPulse.

---

## Q1. What is Deep Packet Inspection (DPI)?

Deep Packet Inspection (DPI) is a technique used to inspect both packet headers and packet payloads. Unlike traditional packet filtering, DPI examines the content of packets to identify applications, domains, or protocols before making security decisions.

PacketPulse demonstrates this concept by parsing packet headers, extracting payload data, identifying destination domains, and applying rule-based filtering.

---

## Q2. Why did you choose Java for this project?

Java provides a clean object-oriented programming model, a rich collections framework, built-in concurrency support, and platform independence. These features make it suitable for building modular networking and packet-processing applications.

---

## Q3. What is a PCAP file?

A PCAP (Packet Capture) file stores packets captured from a network interface.

Each packet contains:

• Ethernet Header

• IP Header

• TCP/UDP Header

• Payload

PacketPulse processes these packets offline instead of capturing live network traffic.

---

## Q4. What is a FiveTuple?

A FiveTuple uniquely identifies a network flow.

It consists of:

• Source IP Address

• Destination IP Address

• Source Port

• Destination Port

• Protocol

PacketPulse generates a FiveTuple for every parsed packet.

---

## Q5. Why is FiveTuple important?

Using FiveTuple allows packets belonging to the same communication session to be grouped together. This concept is commonly used in firewalls, routers, NAT devices, and DPI systems.

---

## Q6. What is SNI?

SNI (Server Name Indication) is an extension of the TLS protocol.

It allows the client to indicate the destination hostname during the TLS handshake.

PacketPulse extracts SNI information from supported TLS Client Hello packets to identify destination domains.

---

## Q7. What is the role of PolicyEngine?

PolicyEngine checks whether the detected domain exists in the predefined blocked domain list.

If found:

Packet → DROPPED

Otherwise:

Packet → FORWARDED

---

## Q8. Which Java Collections are used?

HashMap

Used for traffic statistics.

HashSet

Used for blocked domain lookup.

ArrayList

Used for storing PacketJob objects.

LinkedBlockingQueue

Used for thread-safe packet queues.

LongAdder

Used for packet counters.

---

## Q9. Why HashSet?

HashSet provides fast lookup with average O(1) time complexity.

Since PolicyEngine frequently checks whether a domain exists inside the blocked list, HashSet provides efficient performance.

---

## Q10. Why LinkedBlockingQueue?

LinkedBlockingQueue provides thread-safe communication between producer and consumer threads.

PacketPulse wraps it inside TSQueue for reusable packet processing.

---

## Q11. Why LongAdder instead of AtomicLong?

LongAdder provides better scalability under concurrent updates.

It reduces contention when multiple threads update counters simultaneously.

---

## Q12. Does PacketPulse capture live packets?

No.

The current implementation processes offline PCAP files only.

---

## Q13. Does PacketPulse support IPv6?

No.

The current implementation parses IPv4 packets.

---

## Q14. Can PacketPulse inspect HTTPS traffic?

PacketPulse cannot decrypt HTTPS traffic.

However, it can inspect TLS Client Hello packets and extract Server Name Indication (SNI) when available.

---

## Q15. Why modular architecture?

Separating the project into packages improves:

• Readability

• Maintainability

• Scalability

• Code Reusability

---

# 61. Learning Outcomes

Developing PacketPulse helped understand:

• Java Programming

• Object-Oriented Programming

• Java Collections

• Packet Parsing

• Ethernet Frame Structure

• IPv4 Header

• TCP Header

• UDP Header

• TLS Client Hello

• Server Name Indication

• FiveTuple

• PCAP File Format

• Rule-Based Filtering

• Network Security Fundamentals

• Software Design

---

# 62. Challenges Faced During Development

Some challenges encountered while building PacketPulse included:

• Understanding the PCAP file format

• Parsing binary packet data

• Calculating protocol header offsets

• Extracting payload safely

• Handling malformed packets

• Extracting domain information

• Designing reusable data models

• Organizing the project into packages

Each challenge was solved by dividing the project into smaller reusable components.

---

# 63. Key Design Decisions

The following decisions were taken while designing PacketPulse.

• Separate parser logic from business logic.

• Use model classes for packet representation.

• Keep PolicyEngine independent from parsing logic.

• Implement reusable queue and load balancing classes.

• Maintain modular package structure.

• Generate a readable console report instead of raw packet output.

These decisions improve readability and make future enhancements easier.

---

# 64. Repository Information

Repository Name

PacketPulse - Deep Packet Inspection Engine

Programming Language

Java

Documentation

README.md

PROJECT_DETAILS.md

Sample Input

test_dpi.pcap

License

MIT License

---

# 65. Project Summary

PacketPulse is an educational Java project that demonstrates how offline network packets can be parsed, inspected, classified, and filtered using rule-based policies.

The application reads packets from a PCAP file, parses Ethernet, IPv4, TCP, and UDP headers, extracts payload information, identifies destination domains, applies security policies, and generates a structured traffic analysis report.

The project emphasizes clean architecture, modular design, and practical implementation of networking concepts. While simplified for educational purposes, it provides a strong foundation for understanding packet inspection and Java-based network analysis.

---

# 66. Acknowledgements

This project was developed as a personal learning project to strengthen knowledge in:

• Java Programming

• Computer Networks

• Packet Processing

• Deep Packet Inspection

• Network Security Fundamentals

The implementation focuses on understanding core concepts rather than building a production-ready network security product.

---

# End of Document

Thank you for exploring the PacketPulse project.

If you have any questions or suggestions, feel free to open an issue or contribute through GitHub.