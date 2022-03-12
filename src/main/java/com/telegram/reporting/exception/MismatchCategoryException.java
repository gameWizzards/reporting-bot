package com.telegram.reporting.exception;

public class MismatchCategoryException extends RuntimeException {

    public MismatchCategoryException() {
    }

    public MismatchCategoryException(String message) {
        super(message);
    }

    public MismatchCategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
