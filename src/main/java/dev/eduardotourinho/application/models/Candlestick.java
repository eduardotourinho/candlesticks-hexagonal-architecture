package dev.eduardotourinho.application.models;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Candlestick(
        Instant openTimestamp,
        Instant closeTimestamp,
        Double openPrice,
        Double closingPrice,
        Double highPrice,
        Double lowPrice) {

}
