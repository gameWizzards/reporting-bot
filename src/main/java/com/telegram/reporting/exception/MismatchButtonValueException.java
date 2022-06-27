package com.telegram.reporting.exception;

public class MismatchButtonValueException extends RuntimeException {

    public MismatchButtonValueException() {
    }

    public MismatchButtonValueException(String message) {
        super(message);
    }

    public MismatchButtonValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
