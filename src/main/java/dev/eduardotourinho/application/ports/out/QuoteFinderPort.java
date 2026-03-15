package dev.eduardotourinho.application.ports.out;

import dev.eduardotourinho.application.models.Quote;

import java.time.Instant;
import java.util.List;

public interface QuoteFinderPort {

    List<Quote> fetchQuotes(String isin, Instant initialTimestamp, Instant endTimestamp);
}
