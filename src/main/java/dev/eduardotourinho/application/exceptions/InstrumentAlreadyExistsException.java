package dev.eduardotourinho.application.exceptions;

public class InstrumentAlreadyExistsException extends RuntimeException {

    public InstrumentAlreadyExistsException(String isin) {
        super("Instrument already exists: " + isin);
    }
}