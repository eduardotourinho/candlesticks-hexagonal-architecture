package dev.eduardotourinho.application.exceptions;

public class InstrumentNotFoundException extends RuntimeException {

    public InstrumentNotFoundException(String isin) {
        super("Instrument not found: " + isin);
    }
}