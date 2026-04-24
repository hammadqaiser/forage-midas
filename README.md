# Midas Core — Financial Transaction Engine

> A production-grade financial transaction processing microservice built with **Spring Boot**, **Apache Kafka**, **H2/JPA**, and **REST APIs** — developed as part of the **JPMorganChase Software Engineering Virtual Experience** on Forage.

---

## What is Midas Core?

Midas Core is the backend service responsible for **receiving, validating, persisting, and exposing financial transactions at scale**. It simulates the kind of high-throughput, resilient transaction infrastructure used inside real enterprise banking systems.

The system is designed around three core responsibilities:

- **Ingest** — consume financial transactions from a Kafka message queue
- **Process** — validate each transaction against business rules and persist valid ones to a SQL database
- **Expose** — serve processed transaction data through a REST API

---

## System Architecture

```
                        ┌─────────────────────────────────┐
                        │          Midas Core              │
                        │                                  │
  Trading Frontend ──▶  │  Kafka Listener                  │
  (Kafka Producer)      │       │                          │
                        │       ▼                          │
                        │  Transaction Validator           │
                        │       │                          │
                        │       ▼                          │
                        │  H2 Database (JPA)               │
                        │       │                          │
                        │       ▼                          │
  REST Clients   ◀───   │  REST API Layer                  │
                        └─────────────────────────────────┘
```

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 (LTS) | Core language — LTS chosen for enterprise stability |
| Spring Boot | 3.2.5 | Dependency injection, auto-configuration |
| Apache Kafka | 3.1.4 | Asynchronous message queue for transaction ingestion |
| Spring Data JPA | 3.2.5 | ORM layer — abstracts SQL database access |
| H2 Database | 2.2.224 | In-memory SQL database for development and testing |
| Maven | Latest | Dependency management and reproducible builds |
| JUnit / Spring Boot Test | 3.2.5 | Automated testing framework |
| Testcontainers | 1.19.1 | Docker-based Kafka for integration tests |

---

## Project Tasks Completed

### Task 1 — Project Setup
- Configured Java 17 development environment
- Added and pinned all required Maven dependencies in `pom.xml`
- Externalized Kafka topic configuration to `application.yml`
- Verified setup with automated tests

### Task 2 — Kafka Integration
- Implemented `TransactionListener` — a Kafka consumer using `@KafkaListener`
- Configured JSON deserialization of `Transaction` domain objects
- Used embedded Kafka (`@EmbeddedKafka`) for fully isolated, infrastructure-free tests
- Received and processed 22 financial transactions from a test data file

### Task 3 — H2 Database Integration
- Configured Spring Data JPA with H2 in-memory database
- Designed `TransactionRecord` JPA entity with `@ManyToOne` relationships to `User`
- Implemented business validation rules:
  - Sender ID must exist
  - Recipient ID must exist
  - Sender balance must be >= transaction amount
- Persisted valid transactions and updated account balances atomically
- Discarded invalid transactions with no database modification

### Task 4 — External REST API Integration
- Integrated Midas Core with an external REST API using Spring's `RestTemplate`
- Enriched transaction processing pipeline with data from external services

### Task 5 — Exposing a REST API
- Designed and implemented REST endpoints using Spring MVC (`@RestController`)
- Exposed processed transaction and user balance data to external consumers

---

## Key Engineering Decisions

**Why Kafka for transaction ingestion?**

Kafka decouples the transaction producer (frontend) from the consumer (Midas Core). If Midas Core goes down, transactions remain in the queue and are processed on recovery — no data loss. Kafka also enables horizontal scaling: multiple Midas Core instances can consume from the same topic simultaneously.

**Why SQL (H2) over NoSQL?**

Financial data demands ACID compliance. SQL databases guarantee that partial failures — a balance deduction without the corresponding credit — cannot persist. In financial systems, data integrity takes precedence over raw performance.

**Why dependency pinning in `pom.xml`?**

In regulated financial systems, builds must be reproducible. Floating versions (`latest`) risk silent behavior changes between deployments. Every dependency is locked to an exact version so the application behaves identically across all environments.

**Why externalize Kafka topic to `application.yml`?**

Following the 12-Factor App methodology, configuration is separated from code. The same compiled JAR can be deployed to development, staging, and production by swapping configuration — no recompilation needed.

**Why `@ManyToOne` on `TransactionRecord`?**

Each transaction has one sender and one recipient, but each user can appear in many transactions. A `@ManyToOne` relationship avoids duplicating user data inside every transaction record, maintaining referential integrity and enabling efficient relational queries.

---

## Running the Project

### Prerequisites
- Java 17
- Maven 3.x
- Git

### Clone and Build
```bash
git clone https://github.com/hammadqaiser/midas-core-transaction-engine.git
cd midas-core-transaction-engine
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

### Run All Tests
```bash
mvn test
```

### Run a Specific Task Test
```bash
mvn -Dtest=TaskOneTests test
mvn -Dtest=TaskTwoTests test
mvn -Dtest=TaskThreeTests test
mvn -Dtest=TaskFourTests test
mvn -Dtest=TaskFiveTests test
```

---

## What I Learned

Working on Midas Core gave me hands-on experience with the patterns and technologies used inside real enterprise financial backends:

- How message queues decouple services and enable resilient, asynchronous architectures
- How Spring Boot's dependency injection wires complex infrastructure (Kafka, JPA, REST) with minimal boilerplate
- How to enforce financial business rules at the service layer before persisting data
- How JPA entity relationships model real-world relational data
- How to write integration tests using embedded infrastructure (Kafka, H2) that run with zero external dependencies
- The engineering reasoning behind technology choices in regulated, high-stakes systems

---

## Certificate

Completed as part of the **JPMorganChase Software Engineering Virtual Experience** on [Forage](https://www.theforage.com/completion-certificates/Sj7temL583QAYpHXD/E6McHJDKsQYh79moz_Sj7temL583QAYpHXD_mbmmsnBzugA5Bv3DF_1776970223266_completion_certificate.pdf).

---

## Author

**Hammad Qaiser**
[LinkedIn](https://linkedin.com/in/hammadslash) | [GitHub](https://github.com/hammadqaiser)
