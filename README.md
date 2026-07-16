# PacketPulse — Deep Packet Inspection (DPI) Engine (Java)

A Java-based offline Deep Packet Inspection engine that reads network traffic
from `.pcap` files, parses protocol headers, extracts domain information from
TLS/HTTP traffic, and applies rule-based filtering to classify packets as
FORWARDED or DROPPED.


---

## How to Run

```bash
javac -d bin src/com/packetpulse/model/*.java src/com/packetpulse/parser/*.java src/com/packetpulse/pipeline/*.java src/com/packetpulse/*.java
java -cp bin com.packetpulse.Main
```

Input: `test_dpi.pcap` (sample capture file, must be in the project root)
Output: Console-based packet-by-packet log + final traffic summary report

---

## Project Structure

```
src/com/packetpulse/
├── model/       → Data structures (no logic)
├── parser/      → Packet & protocol parsing logic
├── pipeline/    → Concurrency components (queues, load balancing, workers)
├── Main.java    → Entry point — currently runs the core sequential engine
└── PolicyEngine.java → Simple domain blocklist used by Main
```

---

## What Actually Runs Today (Main.java flow)

This is the real, working execution path when you run the program:

```
test_dpi.pcap
     │
     ▼
PcapReader.readAllPackets()
     │  (reads raw bytes, manually extracts IP/port/payload per packet)
     ▼
Main.extractDomain(payload)
     │  (regex-based domain string match on the raw payload)
     ▼
PolicyEngine.isBlocked(domain)
     │  (checks against a hardcoded 6-domain blocklist)
     ▼
Console log line: FORWARDED / DROPPED
     │
     ▼
Final report (packet counts, per-domain hit counts)
```

### Files involved in this path

**`Main.java`**
Entry point. Reads the pcap file via `PcapReader`, loops over every packet,
extracts a domain via a regex against the raw payload bytes, checks it
against `PolicyEngine`, prints a formatted console report (banner, per-packet
table, final summary with top requested domains).

**`parser/PcapReader.java`**
Opens the `.pcap` file, skips the 24-byte global pcap header, then reads each
packet record (16-byte packet header + raw bytes). For each packet it does
its **own inline parsing** — assumes fixed Ethernet(14)/IP header offsets,
manually pulls source/destination IP and ports out of fixed byte positions,
and copies the first ~150 bytes of payload into `PacketJob.info` as a raw
string for later regex matching.

**`PolicyEngine.java`**
A static, hardcoded blocklist of 6 domains (google.com, facebook.com,
instagram.com, twitter.com, tiktok.com, github.com). Strips a leading `www.`
and does an exact match. This is what actually decides FORWARD vs DROP in
the current build — it does not use `RuleManager`.

---

## Components That Exist But Are Not Yet Wired Into Main

These were built to demonstrate specific concepts (proper protocol parsing,
real TLS SNI extraction, thread-safe rule management, and a multi-threaded
processing pipeline) but **`Main.java` does not currently call them**. They
compile and are structurally complete, but they are not part of the live
execution path above.

### `model/` — Data structures

| File | Purpose |
|---|---|
| `FiveTuple.java` | Represents a network flow: src IP, dst IP, src port, dst port, protocol. Used as the unit of identity for a connection. |
| `Connection.java` | Represents a tracked flow's state — packet/byte counters, detected app type, SNI, timestamps, TCP flag history (SYN/SYN-ACK/FIN seen). |
| `ConnectionState.java` | Enum: NEW → ESTABLISHED → CLASSIFIED → BLOCKED → CLOSED. Models the lifecycle a connection would move through. |
| `AppType.java` | Enum of known applications (YouTube, Facebook, Google, etc.) with a `sniToAppType()` helper that maps an SNI hostname string to an app. |
| `PacketAction.java` | Enum: FORWARD, DROP, INSPECT, LOG_ONLY. |
| `PacketJob.java` | The unit of work passed through the pipeline — holds raw packet bytes, byte offsets for each protocol layer, and metadata. |
| `DPIStats.java` | Atomic counters (`LongAdder`) for total packets/bytes, forwarded/dropped counts, protocol breakdown — designed for concurrent updates from multiple threads. |

### `parser/` — Real protocol parsing (not yet used by Main)

**`PacketParser.java`**
A proper, general-purpose parser: validates Ethernet frame length, checks
EtherType is IPv4, reads the actual IP header length from the header
(instead of assuming it), branches on protocol (TCP/UDP), extracts TCP flags
and computes the real payload offset using the TCP data-offset field. This
is more correct than the inline parsing inside `PcapReader`, but it is
currently unused — `PcapReader` does its own simplified version instead.

**`SNIExtractor.java`**
Parses an actual TLS Client Hello message: verifies the record is a
handshake (`0x16`) and a Client Hello (`0x01`), walks past the session ID,
cipher suites, and compression methods fields, then scans the extensions
list for the SNI extension (type `0x0000`) and pulls out the hostname. Also
has a basic HTTP `Host:` header extractor. This is the real SNI-extraction
logic — `Main.java` currently uses a much cruder regex instead of calling
this class.

### `pipeline/` — Concurrency components (not yet used by Main)

**`TSQueue.java`**
A thread-safe blocking queue wrapper around `LinkedBlockingQueue`, with
`push`/`pop`/`popWithTimeout` and a `shutdown()` flag for clean worker
termination.

**`LoadBalancer.java`**
Designed to sit between a packet reader and a pool of worker threads: pulls
jobs from its own input queue, hashes the packet's `FiveTuple`, and
dispatches it to one of several `FastPathProcessor` queues — so that all
packets belonging to the same flow are always sent to the same worker
(consistent hashing).

**`FastPathProcessor.java`**
A worker thread intended to pull jobs from its queue and process them (parse
→ extract SNI → check rules → classify). In the current code, `run()` only
pops jobs and increments a counter — the actual processing logic has not
been added yet.

**`RuleManager.java`**
A thread-safe rule engine using `ReentrantReadWriteLock` per rule type (IP,
app, domain, port) so multiple worker threads can check rules concurrently
without blocking each other on writes. Supports blocking by IP, app type,
domain (including subdomain matching), and port, and returns a `BlockReason`
explaining which rule matched. This is more capable than `PolicyEngine`, but
is not currently instantiated or called from `Main`.

---

## Known Issue

`FiveTuple.equals()` currently always returns `true` regardless of the two
objects being compared. This has no effect on the current execution path
(which doesn't use `FiveTuple` as a map key), but it would cause incorrect
flow deduplication if `FiveTuple` were used as a `HashMap`/`HashSet` key —
which is exactly the intended use once the pipeline is wired up. Needs a
proper field-by-field comparison.

---

## Why the Codebase Looks Like This

The `parser/` and `pipeline/` packages were built to explore how a real DPI
engine would parse protocols correctly and process packets concurrently at
scale, before wiring that into the main execution flow. `Main.java` today
runs the simpler, sequential version so there's always a working demo.

---

## Planned Next Steps

- [ ] Fix `FiveTuple.equals()`
- [ ] Route `PcapReader` through `PacketParser` instead of its own inline parsing
- [ ] Replace the regex domain match in `Main` with `SNIExtractor.extractTLS()`
- [ ] Replace `PolicyEngine` with `RuleManager` for rule checks
- [ ] Start `LoadBalancer` + `FastPathProcessor` threads from `Main` and route
      packets through the queue-based pipeline instead of a simple for-loop
- [ ] Fill in `FastPathProcessor.run()` with real parse → classify → rule-check logic
- [ ] Track connection state using `Connection` / `ConnectionState`, and
      report using `DPIStats` instead of a plain `HashMap`

---

## Skills Demonstrated

- Java (OOP, exceptions, file I/O, collections, concurrency primitives)
- Manual byte-level protocol parsing (Ethernet, IPv4, TCP, UDP)
- TLS Client Hello structure and SNI extraction
- Thread-safe data structures (`ReentrantReadWriteLock`, blocking queues, `LongAdder`)
- Producer-consumer / load-balanced worker pool design (architected, integration in progress)

---

## License

MIT