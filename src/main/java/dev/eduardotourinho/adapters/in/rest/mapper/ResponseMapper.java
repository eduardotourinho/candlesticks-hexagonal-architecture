package dev.eduardotourinho.adapters.in.rest.mapper;

import dev.eduardotourinho.adapters.in.rest.models.CandlestickResponse;
import dev.eduardotourinho.application.models.Candlestick;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class ResponseMapper {

    public CandlestickResponse.Candlestick responseFrom(Candlestick candlestick) {
        return CandlestickResponse.Candlestick.builder()
                .openTimestamp(DateTimeFormatter.ISO_INSTANT.format(candlestick.openTimestamp().atOffset(ZoneOffset.UTC)))
                .closeTimestamp(DateTimeFormatter.ISO_INSTANT.format(candlestick.closeTimestamp().atOffset(ZoneOffset.UTC)))
                .openPrice(roundValue(candlestick.openPrice()))
                .closePrice(roundValue(candlestick.closePrice()))
                .highPrice(roundValue(candlestick.highPrice()))
                .lowPrice(roundValue(candlestick.lowPrice()))
                .build();
    }

    private double roundValue(double value) {
        return (double) Math.round(value * 100) / 100;
    }
}
