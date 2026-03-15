package dev.eduardotourinho.adapters.in.websocket.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardotourinho.application.exceptions.InstrumentNotFoundException;
import dev.eduardotourinho.application.ports.in.ManageQuoteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotesStreamHandlerTest {

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private ManageQuoteUseCase quoteManager;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private QuotesStreamHandler subject;

    @Test
    void shouldSaveQuoteOnQuoteEvent() {
        var message = new TextMessage("""
                {"data":{"isin":"US0378331005","price":174.55}}
                """);

        assertDoesNotThrow(() -> subject.handleTextMessage(session, message));

        verify(quoteManager).saveQuote("US0378331005", 174.55);
        verifyNoMoreInteractions(quoteManager);
    }

    @Test
    void shouldNotPropagateExceptionOnMalformedJson() {
        var message = new TextMessage("not valid json");

        assertDoesNotThrow(() -> subject.handleTextMessage(session, message));

        verifyNoInteractions(quoteManager);
    }

    @Test
    void shouldNotPropagateExceptionWhenUseCaseThrows() {
        var message = new TextMessage("""
                {"data":{"isin":"US0378331005","price":174.55}}
                """);
        doThrow(new InstrumentNotFoundException("US0378331005"))
                .when(quoteManager).saveQuote("US0378331005", 174.55);

        assertDoesNotThrow(() -> subject.handleTextMessage(session, message));
    }
}
