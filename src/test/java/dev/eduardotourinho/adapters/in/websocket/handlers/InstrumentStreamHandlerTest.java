package dev.eduardotourinho.adapters.in.websocket.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardotourinho.application.exceptions.InstrumentAlreadyExistsException;
import dev.eduardotourinho.application.ports.in.ManageInstrumentUseCase;
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
class InstrumentStreamHandlerTest {

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private ManageInstrumentUseCase instrumentManager;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private InstrumentStreamHandler subject;

    @Test
    void shouldAddInstrumentOnAddEvent() {
        var message = new TextMessage("""
                {"type":"ADD","data":{"isin":"US0378331005","description":"Apple Inc."}}
                """);

        assertDoesNotThrow(() -> subject.handleTextMessage(session, message));

        verify(instrumentManager).addInstrument("US0378331005", "Apple Inc.");
        verifyNoMoreInteractions(instrumentManager);
    }

    @Test
    void shouldDeleteInstrumentOnDeleteEvent() {
        var message = new TextMessage("""
                {"type":"DELETE","data":{"isin":"US0378331005","description":null}}
                """);

        assertDoesNotThrow(() -> subject.handleTextMessage(session, message));

        verify(instrumentManager).deleteInstrument("US0378331005");
        verifyNoMoreInteractions(instrumentManager);
    }

    @Test
    void shouldNotPropagateExceptionOnMalformedJson() {
        var message = new TextMessage("not valid json");

        assertDoesNotThrow(() -> subject.handleTextMessage(session, message));

        verifyNoInteractions(instrumentManager);
    }

    @Test
    void shouldNotPropagateExceptionWhenUseCaseThrows() {
        var message = new TextMessage("""
                {"type":"ADD","data":{"isin":"US0378331005","description":"Apple Inc."}}
                """);
        doThrow(new InstrumentAlreadyExistsException("US0378331005"))
                .when(instrumentManager).addInstrument("US0378331005", "Apple Inc.");

        assertDoesNotThrow(() -> subject.handleTextMessage(session, message));
    }
}
