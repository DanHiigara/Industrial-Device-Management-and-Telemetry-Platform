# Industrial Device Management & Telemetry Platform (Java 17 / Spring Boot)

An enterprise-style, Java-centric backend platform for managing distributed devices, ingesting high-throughput telemetry, running alert rules, and exposing secure APIs to web/mobile clients.

## Key highlights (what clients care about)
- **Java 17+**, Spring Boot microservices, clean layering
- **PostgreSQL** (metadata), **InfluxDB** (time series)
- **Kafka** event streaming (ingestion → alert evaluation)
- **JWT auth** (roles: ADMIN / ENGINEER / VIEWER), API-key access for devices
- Resilience patterns: retries, timeouts, circuit breakers (Resilience4j)
- Async processing: `CompletableFuture`, bounded executors, back-pressure at ingestion
- OpenAPI / Swagger UI per service

---

## Architecture

```text
Devices / Simulator
   ├─ HTTP (Telemetry POST)
   └─ (optional) MQTT/TCP (future)

                 ┌──────────────────┐
                 │   API Gateway     │  (Spring Cloud Gateway, JWT validation)
                 └─────────┬────────┘
                           │
     ┌─────────────────────┼────────────────────────┐
     │                     │                        │
┌────▼─────┐        ┌──────▼────────┐        ┌──────▼───────┐
│ Auth     │        │ Device Service │        │ Telemetry     │
│ Service  │        │ (Postgres)     │        │ Service       │
│ (JWT)    │        │                │        │ (Influx + Kafka)
└────┬─────┘        └──────┬─────────┘        └──────┬───────┘
     │                      │                         │
     │                      │                         │ Kafka topic:
     │                      │                         │ telemetry.normalized
     │                      │                         ▼
     │                      │                 ┌───────────────┐
     │                      │                 │ Alert Service  │
     │                      │                 │ (rules + Kafka)│
     │                      │                 └───────────────┘
     │                      │
     ▼                      ▼
 PostgreSQL            PostgreSQL
 (users/auth)          (devices/alerts)

InfluxDB holds time-series telemetry.

```

---

## Repo layout

```text
java-device-platform/
  api-gateway/
  auth-service/
  device-service/
  telemetry-service/
  alert-service/
  simulator-cli/
  common-lib/
  docker-compose.yml
  pom.xml
```

---

## Quick start (Docker Compose)

1) Build jars:
```bash
mvn -q -DskipTests package
```

2) Run:
```bash
docker compose up --build
```

Services:
- Gateway: http://localhost:8080
- Auth: http://localhost:8081/swagger-ui.html
- Device: http://localhost:8082/swagger-ui.html
- Telemetry: http://localhost:8083/swagger-ui.html
- Alert: http://localhost:8084/swagger-ui.html
- InfluxDB: http://localhost:8086
- Kafka (Redpanda): http://localhost:9092

---

## Example flow

### 1) Register a user and login (JWT)
Use Swagger UI at Auth Service or call via gateway:

- `POST /api/auth/register`
- `POST /api/auth/login`

Copy the returned `accessToken` and use it as:
`Authorization: Bearer <token>`

### 2) Create a device
- `POST /api/devices` (ADMIN/ENGINEER)
Returns an `apiKey` used by devices.

### 3) Ingest telemetry (device → telemetry-service)
POST via gateway:
- `POST /api/telemetry` with header `X-API-KEY: <device apiKey>`

Payload:
```json
{
  "deviceId": "WT-ESP32-001",
  "timestamp": "2026-01-05T14:32:10Z",
  "temperature": 42.7,
  "vibration": 0.012,
  "status": "OK"
}
```

This:
- validates payload
- checks device API key with device-service
- writes to InfluxDB
- publishes normalized event to Kafka

### 4) Alert evaluation
Alert-service consumes Kafka events and evaluates active rules:
- Threshold rules: temperature/vibration above limit
- Device offline: "lastSeen" too old (scheduled check)

---

## Security model
- Users authenticate via Auth Service → JWT (HS256)
- API Gateway validates JWT and forwards requests
- Device telemetry uses **API key** header (X-API-KEY) validated against Device Service
- Internal service-to-service calls are on the Docker network

---

## Performance considerations
- Telemetry ingestion is non-blocking where possible; Influx write is async.
- Kafka publish uses batching and retries.
- Back-pressure via a bounded executor + queue; overload returns 429 with retry-after.
- Circuit breakers on remote calls (device-service validation).
- API versioning supported (Gateway routes `/api/v1/...` alias ready).

---

## Screenshots
Add:
- Swagger UI screenshots
- a simple dashboard (optional frontend)

---

## License
MIT
