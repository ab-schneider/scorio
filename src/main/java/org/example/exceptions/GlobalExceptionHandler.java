package org.example.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorBody(int status, String error, String message, Instant timestamp) {}

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorBody> handleResponseStatus(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorBody body = new ErrorBody(
                status.value(),
                status.getReasonPhrase(),
                exception.getReason(),
                Instant.now()
        );

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception ex) {
        ErrorBody body = new ErrorBody(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}