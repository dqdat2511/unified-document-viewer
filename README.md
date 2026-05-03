# Keyloop Technical Assessment — Scenario D: Unified Document Viewer

> **Candidate submission** | Scenario D — Operate Domain | Backend implementation

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Running Tests](#running-tests)
- [API Reference](#api-reference)
- [Observability](#observability)
- [Project Structure](#project-structure)
- [Design Decisions](#design-decisions)
- [AI Collaboration Narrative](#ai-collaboration-narrative)

---

## Overview

This project implements **Scenario D — The Unified Document Viewer**: a backend system that provides a single search interface for vehicle documents, aggregating results from two independent external systems (Sales System and Service System) in parallel, identified by Vehicle Identification Number (VIN).

### What was built

- **Auth Service** (port 8081) — Issues and validates JWT tokens
- **Document Service** (port 8082) — Core aggregation engine; fetches documents from both external systems in parallel, caches results in Redis
- **Gateway Service** (port 8080) — Single entry point; JWT validation, routing to downstream services
- **Mock APIs** — WireMock stubs for Sales System (port 8090) and Service System (port 8091)
- **Observability stack** — Prometheus (port 9090) + Grafana (port 3000) with pre-built dashboards

### Core requirements coverage

| Requirement | Implementation |
|---|---|
| Unified VIN search interface | `GET /api/v1/documents/{vin}` via Gateway |
| Parallel requests to both external APIs | `CompletableFuture` fan-out in `DocumentFetchService` |
| Aggregated view with source labels | Response includes `source: "SALES"` / `source: "SERVICE"` per document |

---

## Architecture

```
Client
  │  JWT Bearer Token
  ▼
Gateway Service (:8080)
  │  JWT validation via Auth Service
  │  Spring Cloud Gateway routing
  ├──► Auth Service (:8081) — POST /api/v1/auth/**
  └──► Document Service (:8082) — GET /api/v1/documents/**
            │
            │  Hexagonal Architecture (Ports & Adapters)
            │
            ├── Redis (cache — 5 min TTL)
            ├── MySQL (active VIN registry)
            ├──► Sales Mock API (:8090)   ─┐ parallel
            └──► Service Mock API (:8091) ─┘ CompletableFuture
```

### Hexagonal Architecture layers

```
document-service/
├── domain/               # Pure business models — zero framework imports
│   ├── DocumentItem
│   ├── AggregatedDocuments
│   └── ActiveVin
├── application/
│   ├── port/in/          # INPUT PORTS (use case interfaces)
│   │   └── AggregateDocumentsUseCase
│   ├── port/out/         # OUTPUT PORTS (what the domain needs)
│   │   ├── DocumentProviderPort   ← adapters implement this
│   │   ├── DocumentRepositoryPort
│   │   └── CachePort
│   └── service/          # Use case implementations
│       ├── DocumentAggregationService
│       └── DocumentFetchService
└── adapters/
    ├── in/web/            # PRIMARY ADAPTER — REST Controller
    └── out/
        ├── client/        # SECONDARY ADAPTERS — Feign + WireMock
        │   ├── SalesDocumentProviderAdapter  (implements DocumentProviderPort)
        │   └── ServiceDocumentProviderAdapter (implements DocumentProviderPort)
        └── persistence/   # SECONDARY ADAPTER — JPA/MySQL
```

**SOLID principles applied:**

| Principle | Implementation |
|---|---|
| **S** — Single Responsibility | Controller handles HTTP only; Service orchestrates only; each Adapter translates one system |
| **O** — Open/Closed | Adding a new document source = new `DocumentProviderPort` bean; zero changes to `DocumentFetchService` |
| **L** — Liskov Substitution | Both provider adapters are fully interchangeable; tests mock the port, not the Feign client |
| **I** — Interface Segregation | `DocumentProviderPort`, `DocumentRepositoryPort`, `CachePort` are separate narrow interfaces |
| **D** — Dependency Inversion | `DocumentFetchService` depends only on `DocumentProviderPort`; Spring wires concrete adapters |

---

## Technology Stack

| Technology | Version | Justification |
|---|---|---|
| Java | 24 | Latest release, virtual threads, records, improved performance |
| Spring Boot | 3.3.x | Production-grade autoconfiguration |
| Spring Cloud Gateway | 2023.x | Reactive proxy, JWT filter hook |
| Spring Cloud OpenFeign | 2023.x | Declarative HTTP clients |
| MySQL | 8.0 | ACID, active VIN registry |
| Redis | 7.2 | Sub-millisecond document cache (5 min TTL) |
| Flyway | Bundled | Version-controlled schema migrations |
| WireMock | 3.9.1 | Realistic HTTP-level external API mocks |
| Prometheus + Grafana | Latest stable | Metrics scraping, pre-built dashboards |

---

## Prerequisites

| Tool | Minimum version |
|---|---|
| Docker | 24.x |
| Docker Compose | 2.x |
| Java | 24 (build only) |
| Maven | 3.9 (build only) |

---

## Quick Start

### Option 1 — Docker Compose (recommended)

```bash
# 1. Clone the repository
git clone <repository-url>
cd <repository-folder>

# 2. Start the full stack
docker compose up --build
```

Wait ~60 seconds for all services to start. You will see `Started DocumentServiceApplication` in the logs.

| Service | URL | Description |
|---|---|---|
| Gateway | http://localhost:8080 | Single entry point for all API calls |
| Auth Service | http://localhost:8081 | Direct access (bypass gateway) |
| Document Service | http://localhost:8082 | Direct access (bypass gateway) |
| Sales Mock | http://localhost:8090 | WireMock — simulates Sales System |
| Service Mock | http://localhost:8091 | WireMock — simulates Service System |
| Prometheus | http://localhost:9090 | Metrics store |
| Grafana | http://localhost:3000 | Dashboards (admin / admin) |

### Option 2 — Local Maven build

```bash
# Start infrastructure only
docker compose up mysql redis sales-mock service-mock -d

# Build all modules
mvn clean package -DskipTests

# Run each service in a separate terminal
cd auth-service     && mvn spring-boot:run  # terminal 1
cd document-service && mvn spring-boot:run  # terminal 2
cd gateway-service  && mvn spring-boot:run  # terminal 3
```

---

## Running Tests

```bash
# Run all tests
mvn test

# Document service only
cd document-service && mvn test

# Auth service only
cd auth-service && mvn test

# With verbose output
mvn test -pl document-service -Dsurefire.useFile=false
```

### Test coverage summary

| Test class | Layer | Scenarios covered |
|---|---|---|
| `DocumentAggregationServiceTest` | Application | Cache hit, cache miss + external fetch, VIN not found, results cached after fetch, empty result |
| `DocumentFetchServiceTest` | Application | Both providers called, partial failure isolation, all providers empty |
| `SalesDocumentProviderAdapterTest` | Adapter | DTO → domain mapping, fault isolation on client exception, provider name |
| `DocumentControllerTest` | Web | HTTP 200, 400 (short VIN), 400 (forbidden chars I/O/Q), 404 |
| `AuthApplicationServiceTest` | Application | Valid BCrypt login, wrong password, unknown user, token validation delegation, malformed header |

---

## API Reference

### Authentication

All document endpoints require a `Bearer` JWT token.

#### POST `/api/v1/auth/login`

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```
docker compose up -d mysql redis sales-mock service-mock
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 600,
  "role": "ADMIN"
}
```

**Demo credentials:**

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `viewer` | `viewer123` | VIEWER |

> Passwords are BCrypt-hashed (strength 10) in the database. Never stored as plain text.

---

#### GET `/api/v1/documents/{vin}`

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -s http://localhost:8080/api/v1/documents/1HGBH41JXMN109186 \
  -H "Authorization: Bearer $TOKEN" | jq .
```

**Response `200 OK`:**
```json
{
  "vin": "1HGBH41JXMN109186",
  "total": 5,
  "documents": [
    {
      "id": "SALES-001",
      "name": "Purchase Invoice",
      "type": "PDF",
      "source": "SALES",
      "url": "https://sales-system.example.com/docs/SALES-001.pdf",
      "createdAt": "2024-01-15T10:30:00Z"
    },
    {
      "id": "SVC-001",
      "name": "Oil Change Report",
      "type": "PDF",
      "source": "SERVICE",
      "url": "https://service-system.example.com/docs/SVC-001.pdf",
      "createdAt": "2024-03-10T08:00:00Z"
    }
  ]
}
```

**VIN format:** exactly 17 alphanumeric characters; letters `I`, `O`, `Q` not permitted (ISO 3779).

**Sample active VINs (seeded):**
```
1HGBH41JXMN109186
WVWZZZ1JZXW000001
JH4KA7532PC000001
```

**Error responses:**

| Status | Cause |
|---|---|
| `400` | VIN format invalid |
| `401` | Missing or expired JWT |
| `404` | VIN not in active registry |

---

### One-liner end-to-end test

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.token')

curl -s http://localhost:8080/api/v1/documents/1HGBH41JXMN109186 \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

## Observability

### Structured logging with TraceId

Every request carries a `traceId` propagated across all three services:

```
INFO  [traceId=8f3a1b2c] DocumentAggregationService - Aggregating documents for vin=1HGBH41JXMN109186
INFO  [traceId=8f3a1b2c] DocumentFetchService - Fetching from 2 provider(s)
INFO  [traceId=8f3a1b2c] DocumentFetchService - Combined 5 document(s) for vin=1HGBH41JXMN109186
```

### Prometheus metrics

```bash
curl http://localhost:8082/actuator/prometheus | grep "documents_"
```

| Metric | Type | Description |
|---|---|---|
| `documents_aggregation_latency` | Timer | End-to-end aggregation time |
| `documents_cache_hit_total` | Counter | Redis cache hits |
| `documents_cache_miss_total` | Counter | Redis cache misses |

### Health checks

```bash
curl http://localhost:8080/actuator/health   # gateway
curl http://localhost:8081/actuator/health   # auth
curl http://localhost:8082/actuator/health   # document
```

### Grafana

Open http://localhost:3000 (admin / admin). The **Document Viewer Overview** dashboard loads automatically.

---

## Project Structure

```
.
├── docker-compose.yml
├── pom.xml                              # Multi-module Maven root
├── auth-service/
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/example/auth/
│       │   ├── adapters/in/web/         AuthController, GlobalExceptionHandler
│       │   ├── adapters/out/security/   JwtTokenAdapter
│       │   ├── adapters/out/persistence/ JpaUserAccountAdapter
│       │   ├── application/service/     AuthApplicationService
│       │   └── domain/model/            UserAccount, AuthenticatedUser
│       └── test/                        AuthApplicationServiceTest
├── document-service/
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/example/document/
│       │   ├── domain/                  DocumentItem, AggregatedDocuments, ActiveVin
│       │   ├── application/port/in/     AggregateDocumentsUseCase
│       │   ├── application/port/out/    DocumentProviderPort, CachePort, DocumentRepositoryPort
│       │   ├── application/service/     DocumentAggregationService, DocumentFetchService
│       │   ├── adapters/in/web/         DocumentController
│       │   └── adapters/out/
│       │       ├── client/              SalesDocumentProviderAdapter, ServiceDocumentProviderAdapter
│       │       └── persistence/         JpaActiveVinRepositoryAdapter
│       └── test/
│           ├── application/             DocumentAggregationServiceTest, DocumentFetchServiceTest
│           └── adapter/                 DocumentControllerTest, SalesDocumentProviderAdapterTest
├── gateway-service/
│   ├── Dockerfile
│   └── src/main/java/com/example/gateway/
│       ├── adapters/in/web/filter/      JwtValidationFilter
│       ├── adapters/out/security/       AuthServiceTokenValidationAdapter
│       └── application/service/         GatewayAccessValidationService
├── mocks/
│   ├── sales/mappings/documents.json    WireMock — Sales System stub
│   └── service/mappings/documents.json  WireMock — Service System stub
├── monitoring/
│   ├── prometheus/prometheus.yml
│   └── grafana/                         Dashboards + provisioning
└── openapi/openapi.yaml                 OpenAPI 3.0 specification
```

---

## Design Decisions

| Decision | Rationale |
|---|---|
| **Hexagonal architecture** | Business logic is fully isolated from framework details; adapters can be swapped without touching any use case |
| **Parallel fetch with `CompletableFuture`** | Sales and Service APIs are independent; sequential calls double latency for no benefit |
| **Partial result on provider failure** | Returning available documents is better UX than failing the entire request when one system is down |
| **Redis cache (5 min TTL)** | Vehicle documents are rarely updated minute-to-minute; caching removes repeated round-trips to both APIs |
| **Active VIN registry in MySQL** | Acts as an authorization gate — only registered vehicles return results, preventing arbitrary VIN enumeration |
| **Flyway over `ddl-auto: update`** | Migrations are version-controlled, auditable, and safe across environments |
| **WireMock for mock APIs** | HTTP-level mocking behaves identically to a real API including headers, status codes, and network timing |
| **BCrypt (strength 10)** | Adaptive hashing; industry standard for password storage |
| **Gateway as single entry point** | Centralised JWT validation; downstream services do not implement auth independently |

---

## AI Collaboration Narrative

> This section fulfils the Keyloop requirement: *"Describe your high-level strategy for guiding the AI, your process for verifying and refining its output, and how you ensured the final quality of the code."*

### My approach

After identifying the architecture and setting up the project skeleton based on layers and domain boundaries, I did not ask the AI to generate the entire system at once.

Instead, I broke the problem down layer by layer — domain, ports, use cases, then adapters — and asked the AI to implement each part with explicit constraints on dependencies and boundaries. Throughout the process, I acted as a technical lead: controlling how components interacted with each other, and reviewing and correcting whenever the AI produced code that violated architectural principles or best practices.

The goal was to ensure the system stayed on the right track — never violating rules like layer isolation, dependency inversion, and clean architecture.

### How I directed the AI at each layer

**Domain layer first.** I established the domain models before any infrastructure existed. Each prompt included hard constraints:

> *"Write `DocumentItem`, `AggregatedDocuments`, and `ActiveVin` as pure Java classes. No Spring imports. No `@Setter` — domain models must be immutable after construction. Use `@Getter`, `@AllArgsConstructor`, `@NoArgsConstructor` only."*

This order was intentional: if the domain is clean before adapters are written, there is nothing for infrastructure concerns to leak into.

**Ports as contracts, not implementations.** Before writing any use case logic, I defined the port interfaces and explicitly told the AI what each port must not contain:

> *"Write `DocumentProviderPort` with exactly two methods: `fetchDocuments(String vin)` and `providerName()`. Do not include Redis operations, batch methods, or anything infrastructure-specific. Those belong in separate ports."*

This prevented the AI from merging infrastructure concerns into domain-facing interfaces — an ISP violation it would have made if left unprompted.

**Use cases with boundary enforcement.** For each application service, I specified not just what to build but what imports were forbidden:

> *"Write `DocumentFetchService`. It receives `List<DocumentProviderPort>` via constructor injection only. It must not import `SalesServiceClient`, `ServiceServiceClient`, or anything from `adapters.out`. The application layer may only depend on interfaces."*

**Adapters as the only place with infrastructure knowledge.** Once ports were defined, I gave each adapter its mapping responsibility and fault isolation requirement:

> *"Write `SalesDocumentProviderAdapter` implementing `DocumentProviderPort`. Wrap the Feign call in try/catch: on any exception, log a WARN and return `Collections.emptyList()`. The adapter owns fault isolation — exceptions must never propagate to the use case."*

### My verification process

I reviewed every generated file before accepting it, applying the same checklist each time:

- **Layer boundary check** — inspected imports manually; any `application` class importing from `adapters.out` was immediately rejected
- **Interface check** — every dependency injected into the application layer had to be an interface, never a concrete class
- **Immutability check** — domain models must not expose `@Setter` or `@Data`
- **Null safety** — methods returning collections must return empty list, never `null`
- **Failure isolation** — provider exceptions must be caught at the adapter level, not the use case level

### Issues I caught and corrected

| Issue | How I detected it | Fix applied |
|---|---|---|
| `DocumentFetchService` imported `SalesServiceClient` directly — layer violation | Import audit: `grep "adapters.out" DocumentFetchService.java` | Created `DocumentProviderPort`; moved Feign dependency to adapter layer |
| `AuthApplicationService` compared passwords with `String.equals()` | Security review of the full auth flow | Replaced with `PasswordEncoder.matches()` + injected `BCryptPasswordEncoder` |
| `data.sql` stored passwords as plain text | Security review of seed data | Replaced with BCrypt hashes; moved to Flyway `V2__seed_auth_users.sql` |
| `CachePort` double-init via `@PostConstruct` and Spring injection simultaneously | Tracing the bean lifecycle in `DocumentAggregationService` | Removed `@PostConstruct`; constructor injection is the single initialisation path |
| VIN regex `^[A-Z0-9-]{3,32}$` accepted 3-char strings and the hyphen character | Tested edge cases: 3-char VIN, VIN with `-`, VIN containing `I` | Corrected to `^[A-HJ-NPR-Z0-9]{17}$` per ISO 3779 |
| `ddl-auto: update` in production configuration | Production readiness review | Changed to `ddl-auto: validate`; introduced Flyway migrations V1 and V2 |
| Tests covered happy path only | Review of generated test classes — no failure scenarios present | Explicitly prompted for: VIN not found, provider exception, wrong password, malformed auth header |

### What this collaboration demonstrated

Using AI to generate code is straightforward. The harder skill — and the one I focused on — is directing AI as a technical lead would direct an engineer: with clear requirements, explicit constraints, and systematic review of the output.

The layer violations, security gaps, and configuration issues above did not surface immediately. They required me to understand the code well enough to know what to look for — and to correct it before it became a production problem. That ownership is mine, not the AI's.

