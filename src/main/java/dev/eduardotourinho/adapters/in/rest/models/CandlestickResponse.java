package dev.eduardotourinho.adapters.in.rest.models;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandlestickResponse {

    List<Candlestick> candlesticks;

    @Value
    @Builder
    public static class Candlestick {

        String openTimestamp;
        String closeTimestamp;
        Double openPrice;
        Double closePrice;
        Double highPrice;
        Double lowPrice;
    }
}
