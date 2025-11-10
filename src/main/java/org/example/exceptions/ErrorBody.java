package org.example.exceptions;

import java.time.Instant;

public record ErrorBody(int status, String error, String message, Instant timestamp) {}