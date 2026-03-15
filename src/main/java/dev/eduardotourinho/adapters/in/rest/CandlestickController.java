package dev.eduardotourinho.adapters.in.rest;

import dev.eduardotourinho.adapters.in.rest.mapper.ResponseMapper;
import dev.eduardotourinho.adapters.in.rest.models.CandlestickResponse;
import dev.eduardotourinho.application.ports.in.FindCandlesticksUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class CandlestickController {

    private final FindCandlesticksUseCase aggregateCandlesticks;
    private final ResponseMapper responseMapper;

    @GetMapping("/candlesticks")
    public CandlestickResponse getCandlesticks(
            @RequestParam @NotBlank @Pattern(regexp = "[A-Z]{2}[A-Z0-9]{9}[0-9]", message = "must be a valid ISIN") String isin) {
        var candlesticks = aggregateCandlesticks.getCandlesticks(isin);

        var candlestickList = candlesticks.stream()
                .map(responseMapper::responseFrom)
                .toList();

        return CandlestickResponse.builder()
                .candlesticks(candlestickList)
                .build();
    }
}
