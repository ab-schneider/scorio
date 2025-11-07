package org.example.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        if (message.isEmpty()) message = "Validation failed";

        ErrorBody body = new ErrorBody(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now()
        );

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorBody> handleConstraintViolation(ConstraintViolationException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        if (message.isEmpty()) message = "Validation failed";

        ErrorBody body = new ErrorBody(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now()
        );

        return new ResponseEntity<>(body, status);
    }

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorBody> handleIllegalArgument(IllegalArgumentException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorBody body = new ErrorBody(
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                Instant.now()
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<ErrorBody> handleGithubRateLimit(HttpClientErrorException.Forbidden exception) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        String message = "GitHub rate limit exceeded. Please retry shortly.";
        ErrorBody body = new ErrorBody(
                status.value(),
                status.getReasonPhrase(),
                message,
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