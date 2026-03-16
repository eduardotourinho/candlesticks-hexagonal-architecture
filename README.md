# Candlesticks — Hexagonal Architecture Showcase

A portfolio project demonstrating **Hexagonal Architecture** (Ports & Adapters) in a real-time financial data service. The service consumes live instrument and quote WebSocket streams from a partner service, persists them in an embedded H2 database, and exposes OHLC candlestick aggregations via REST.

## Content

- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Design decisions](#design-decisions)
- [Running the Partner Service](#running-the-partner-service)
- [Running the app](#running-the-app)
- [Tests](#tests)

## Tech stack

- Java 17
- Spring Boot 3.0.5
- H2 embedded database
- Flyway (schema migrations)
- Lombok
- Spring Boot Actuator + Micrometer (Prometheus metrics at `/actuator/prometheus`)
- Backstage catalog (`catalog-info.yaml`)

## Architecture

The project follows Hexagonal Architecture, separating business logic from infrastructure concerns:

- **`application/`** — domain models, use case ports (`in/` and `out/`), and service implementations. Has no dependency on frameworks or infrastructure.
- **`adapters/in/`** — driving adapters: REST controller and WebSocket stream handlers.
- **`adapters/out/`** — driven adapters: JPA repositories for instrument and quote persistence.

Communication between the application core and adapters is done exclusively through the defined ports, making each adapter independently replaceable.

### Simplified View

See [docs/architecture.md](docs/architecture.md) for the full diagram.

## Design decisions

- **Real-time aggregation** — candlesticks are aggregated on request rather than pre-calculated, given the assumed low initial request volume.
- **Gap filling** — if no quotes exist for a given minute within the window, that minute is filled with the closing price of the most recent preceding minute. Implemented recursively in `CandlestickService`.
- **30-minute rolling window** — the API returns at most 30 candlesticks. If quotes only exist for a subset of that window, only those minutes are returned.
- **Current minute excluded** — candlesticks for the ongoing minute are not returned, as the window is not yet closed.
- **Cascading deletes** — when an instrument is deleted, its quote history is physically removed from the database.
- **All timestamps in UTC** — stored and returned as ISO 8601 with `Z` suffix.
- **Prices rounded to 2 decimal places.**

## Running the Partner Service

The partner service streams instrument and quote events over WebSocket. Start it before the application.

Using Docker:
```
cd partner-server
docker-compose up -d
```

Using Java:
```
java -jar partner-service-1.0.1-all.jar --port=8032
```

## Running the app

```
./gradlew bootRun
```

The REST API is available at `http://localhost:8080/candlesticks?isin={ISIN}`.

Health and metrics endpoints are exposed via Spring Boot Actuator:
- `GET /actuator/health`
- `GET /actuator/prometheus`

## Tests

```
./gradlew test
```

Tests cover the application service layer, REST controller (integration), WebSocket stream handlers, and storage adapters. The controller integration tests use a mocked use case to verify request validation, response mapping, and error handling in isolation.
