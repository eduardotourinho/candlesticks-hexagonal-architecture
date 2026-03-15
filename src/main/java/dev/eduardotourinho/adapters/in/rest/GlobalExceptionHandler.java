package dev.eduardotourinho.adapters.in.rest;

import dev.eduardotourinho.adapters.in.rest.models.ErrorResponse;
import dev.eduardotourinho.application.exceptions.InstrumentAlreadyExistsException;
import dev.eduardotourinho.application.exceptions.InstrumentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InstrumentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInstrumentNotFound(InstrumentNotFoundException ex) {
        return new ErrorResponse("INSTRUMENT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InstrumentAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInstrumentAlreadyExists(InstrumentAlreadyExistsException ex) {
        return new ErrorResponse("INSTRUMENT_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
    }
}
