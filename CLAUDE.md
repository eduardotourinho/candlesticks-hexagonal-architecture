# Claude Code Context

## Project

Hexagonal architecture showcase — a candlestick aggregation service that consumes real-time instrument and quote WebSocket streams from a partner service, stores them in H2, and exposes OHLC candlestick aggregations via REST.

Originally a job interview coding challenge, being refactored into a personal portfolio showcase.

## Active branch

`showcase-improvements` (based on `refactor-project-name`)

## What has been done

- Package renamed from `com.traderepublic` to `dev.eduardotourinho` (branch: `refactor-project-name`)
- Domain exceptions (`InstrumentNotFoundException`, `InstrumentAlreadyExistsException`) in `application/exceptions/`
- `GlobalExceptionHandler` (`@RestControllerAdvice`) mapping exceptions to 404/409/400/500
- `InstrumentStorageManager` throws instead of silent log+return
- WebSocket handlers catch all exceptions so the stream survives failures
- Input validation: `@Validated` + `@NotBlank` + `@Pattern` (ISIN format `[A-Z]{2}[A-Z0-9]{9}[0-9]`) on `CandlestickController`
- Unit tests for `InstrumentStreamHandler` and `QuotesStreamHandler`
- Spring Boot Actuator + Micrometer Prometheus (`/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`)
- `catalog-info.yaml` for Backstage: System, Component, REST API (OpenAPI), partner WebSocket API (AsyncAPI), Prometheus scrape annotations

## Remaining todos

- Fix `application.yaml` anti-patterns: `open-in-view: true` → `false`, `ddl-auto: update` → `validate`, plain text password → environment variable
- Fix inconsistent timestamp formatting in `ResponseMapper` (openTimestamp uses full date, closeTimestamp uses time-only)
- Convert `Candlestick` from Lombok `@Value` to a Java record (consistency with `Quote`)
- Remove misleading `@CreatedDate` on `QuoteEntity` (annotation is ignored, timestamp is set manually)
- Add Flyway for DB migrations instead of Hibernate DDL auto

## Decisions

- Recursive gap-filling in `CandlestickService.findPastQuotes()` — **do not refactor**, user's decision to keep as-is
- No SonarQube or Kubernetes setup, skip those integrations

## Running tests

```bash
JAVA_HOME=/home/eddie/.jdks/temurin-19.0.2 ./gradlew test
```
