# Candlesticks — Hexagonal Architecture Showcase

A portfolio project demonstrating **Hexagonal Architecture** (Ports & Adapters) in a real-time financial data service. The service consumes live instrument and quote WebSocket streams from a partner service, persists them in an embedded H2 database, and exposes OHLC candlestick aggregations via REST.

## Tech Stack

- Java 17
- Spring Boot 3.0.5
- H2 embedded database
- Flyway (schema migrations)
- Spring Boot Actuator + Micrometer (Prometheus metrics at `/actuator/prometheus`)

## Architecture

The project follows Hexagonal Architecture, separating business logic from infrastructure concerns:

- **`application/`** — domain models, use case ports (`in/` and `out/`), and service implementations. No dependency on frameworks or infrastructure.
- **`adapters/in/`** — driving adapters: REST controller and WebSocket stream handlers.
- **`adapters/out/`** — driven adapters: JPA repositories for instrument and quote persistence.

See the [Architecture](architecture.md) page for a full diagram.

## Design Decisions

- **Real-time aggregation** — candlesticks are aggregated on request rather than pre-calculated.
- **Gap filling** — missing minutes within the 30-minute window are filled with the closing price of the most recent preceding minute.
- **30-minute rolling window** — at most 30 candlesticks are returned.
- **Current minute excluded** — the ongoing minute is not returned until its window closes.
- **Cascading deletes** — deleting an instrument removes its quote history.
- **All timestamps in UTC** — stored and returned as ISO 8601 with `Z` suffix.
- **Prices rounded to 2 decimal places.**

## Running the App

Start the partner service first:

```bash
cd partner-server
docker-compose up -d
```

Then run the application:

```bash
./gradlew bootRun
```

The REST API is available at `http://localhost:8080/candlesticks?isin={ISIN}`.

## Running Tests

```bash
./gradlew test
```
