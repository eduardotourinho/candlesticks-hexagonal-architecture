package dev.eduardotourinho.adapters.in.rest;

import dev.eduardotourinho.adapters.in.rest.mapper.ResponseMapper;
import dev.eduardotourinho.adapters.in.rest.models.CandlestickResponse;
import dev.eduardotourinho.application.ports.in.FindCandlesticksUseCase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CandlestickController {

    private final FindCandlesticksUseCase aggregateCandlesticks;
    private final ResponseMapper responseMapper;

    @GetMapping("/candlesticks")
    public CandlestickResponse getCandlesticks(@RequestParam @NonNull String isin) {
        var candlesticks = aggregateCandlesticks.getCandlesticks(isin);

        var candlestickList = candlesticks.stream()
                .map(responseMapper::responseFrom)
                .toList();

        return CandlestickResponse.builder()
                .candlesticks(candlestickList)
                .build();
    }
}
