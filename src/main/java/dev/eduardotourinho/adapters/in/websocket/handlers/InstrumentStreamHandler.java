package dev.eduardotourinho.adapters.in.websocket.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eduardotourinho.adapters.in.websocket.models.InstrumentEvent;
import dev.eduardotourinho.application.ports.in.ManageInstrumentUseCase;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstrumentStreamHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ManageInstrumentUseCase instrumentManager;


    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("Connected to instruments stream");
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @Nonnull TextMessage message) {
        try {
            var instrumentEvent = objectMapper.readValue(message.getPayload(), InstrumentEvent.class);
            log.debug("InstrumentEvent: {}", instrumentEvent);

            switch (instrumentEvent.type()) {
                case ADD -> instrumentManager.addInstrument(instrumentEvent.data().isin(), instrumentEvent.data().description());
                case DELETE -> instrumentManager.deleteInstrument(instrumentEvent.data().isin());
                default -> log.warn("Unknown instrument event type: {}", instrumentEvent.type());
            }
        } catch (Exception e) {
            log.error("Failed to process instrument event: {}", e.getMessage());
        }
    }
}
