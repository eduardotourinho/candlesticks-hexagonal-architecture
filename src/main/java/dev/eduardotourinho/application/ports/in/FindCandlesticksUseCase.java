package dev.eduardotourinho.application.ports.in;

import dev.eduardotourinho.application.models.Candlestick;

import java.util.List;

public interface FindCandlesticksUseCase {

    List<Candlestick> getCandlesticks(String isin);
}
