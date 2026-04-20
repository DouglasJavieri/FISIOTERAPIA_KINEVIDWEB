package com.fisioterapiakinevid.kinevid.rest.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message, Throwable cause) {
        super(message,cause);
    }
    public ValidationException(String message) {
        super(message);
    }
}