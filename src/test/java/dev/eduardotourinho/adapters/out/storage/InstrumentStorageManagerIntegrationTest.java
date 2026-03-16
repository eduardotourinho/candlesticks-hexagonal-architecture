package dev.eduardotourinho.adapters.out.storage;

import dev.eduardotourinho.adapters.out.storage.models.InstrumentEntity;
import dev.eduardotourinho.adapters.out.storage.models.QuoteEntity;
import dev.eduardotourinho.adapters.out.storage.repositories.InstrumentRepository;
import dev.eduardotourinho.adapters.out.storage.repositories.QuoteRepository;
import dev.eduardotourinho.application.exceptions.InstrumentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration-test")
@SpringBootTest
class InstrumentStorageManagerIntegrationTest {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private InstrumentStorageManager subject;

    @AfterEach
    public void cleanUp() {
        instrumentRepository.deleteAll();
        quoteRepository.deleteAll();
    }

    @Test
    void shouldSaveInstrumentToDb() {
        subject.addInstrument("ABC", "Test instrument");

        var actualEntity = instrumentRepository.findByIsin("ABC");

        assertTrue(actualEntity.isPresent());
        assertEquals("ABC", actualEntity.get().getIsin());
        assertEquals("Test instrument", actualEntity.get().getDescription());
    }

    @Test
    void shouldDeleteInstrumentFromDb() {
        subject.addInstrument("ABC", "Test instrument");

        var savedEntity = instrumentRepository.findByIsin("ABC");
        assertTrue(savedEntity.isPresent());

        subject.deleteInstrument("ABC");
        var deletedEntity = instrumentRepository.findByIsin("ABC");

        assertTrue(deletedEntity.isEmpty());
    }

    @Test
    void shouldDeleteInstrumentAndAllTheQuotesFromDB() {
        subject.addInstrument("ABC", "Test instrument");

        var savedEntity = instrumentRepository.findByIsin("ABC");
        assertTrue(savedEntity.isPresent());

        quoteRepository.saveAll(List.of(
                QuoteEntity.builder()
                        .timestamp(Instant.now().minus(1, ChronoUnit.MINUTES))
                        .price(32.0)
                        .instrument(savedEntity.get())
                        .build(),
                QuoteEntity.builder()
                        .instrument(savedEntity.get())
                        .price(26.4)
                        .timestamp(Instant.now())
                        .build()
        ));

        var savedQuotes = (List<QuoteEntity>) quoteRepository.findAll();
        assertEquals(2, savedQuotes.size());

        subject.deleteInstrument("ABC");

        var deletedEntity = instrumentRepository.findByIsin("ABC");
        var deletedQuotes = (List<QuoteEntity>) quoteRepository.findAll();

        assertTrue(deletedEntity.isEmpty());
        assertTrue(deletedQuotes.isEmpty());
    }

    @Test
    void shouldThrowWhenDeleteInstrumentNotExist() {
        assertThrows(InstrumentNotFoundException.class, () -> subject.deleteInstrument("ABC"));
    }

    @Test
    void shouldAddQuotesToDb() {
        subject.addInstrument("ABC", "Test instrument");

        var savedEntity = instrumentRepository.findByIsin("ABC");
        assertTrue(savedEntity.isPresent());

        subject.saveQuote("ABC", 40.2, Instant.now());
        var actualQuotes = (List<QuoteEntity>) quoteRepository.findAll();

        assertEquals(1, actualQuotes.size());
    }

    @Test
    void shouldThrowWhenAddQuotesToNonExistentInstrument() {
        assertThrows(InstrumentNotFoundException.class, () -> subject.saveQuote("ABC", 40.2, Instant.now()));
    }

    @Test
    void shouldFindAllQuotesFromAPeriod() {
        // Prepare
        var startTimestamp = Instant.parse("2023-04-23T13:30:00.00Z");
        var endTimestamp = Instant.parse("2023-04-23T14:00:00.00Z");

        subject.addInstrument("ABC", "Test instrument");

        var instrumentEntity = instrumentRepository.findByIsin("ABC");
        assertTrue(instrumentEntity.isPresent());

        var quotesToSave = getQuoteEntityList(instrumentEntity.get());
        quoteRepository.saveAll(quotesToSave);

        var allQuotes = (List<QuoteEntity>) quoteRepository.findAll();
        assertEquals(quotesToSave.size(), allQuotes.size());

        // Act
        var actualQuotes = subject.fetchQuotes("ABC", startTimestamp, endTimestamp);

        // Assert
        assertEquals(4, actualQuotes.size());
    }

    @Test
    void shouldThrowIfInstrumentNotFoundWhenFindingQuotes() {
        var startTimestamp = Instant.parse("2023-04-23T13:30:00.00Z");
        var endTimestamp = Instant.parse("2023-04-23T14:00:00.00Z");

        assertThrows(InstrumentNotFoundException.class,
                () -> subject.fetchQuotes("DEF", startTimestamp, endTimestamp));
    }

    private List<QuoteEntity> getQuoteEntityList(InstrumentEntity instrumentEntity) {
        return List.of(
                QuoteEntity.builder()
                        .instrument(instrumentEntity)
                        .price(70.2)
                        .timestamp(Instant.parse("2023-04-23T13:29:59.00Z"))
                        .build(),
                QuoteEntity.builder()
                        .instrument(instrumentEntity)
                        .price(60.2)
                        .timestamp(Instant.parse("2023-04-23T13:30:00.00Z"))
                        .build(),
                QuoteEntity.builder()
                        .instrument(instrumentEntity)
                        .price(50.2)
                        .timestamp(Instant.parse("2023-04-23T13:32:24.00Z"))
                        .build(),
                QuoteEntity.builder()
                        .instrument(instrumentEntity)
                        .price(40.2)
                        .timestamp(Instant.parse("2023-04-23T13:45:48.00Z"))
                        .build(),
                QuoteEntity.builder()
                        .instrument(instrumentEntity)
                        .price(30.2)
                        .timestamp(Instant.parse("2023-04-23T13:59:59.00Z"))
                        .build(),
                QuoteEntity.builder()
                        .instrument(instrumentEntity)
                        .price(30.2)
                        .timestamp(Instant.parse("2023-04-23T14:00:00.00Z"))
                        .build()
        );
    }
}
