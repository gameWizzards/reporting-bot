package com.telegram.reporting.exception;

public class PhoneFormatException extends RuntimeException {
    public PhoneFormatException() {
    }

    public PhoneFormatException(String message) {
        super(message);
    }

    public PhoneFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}