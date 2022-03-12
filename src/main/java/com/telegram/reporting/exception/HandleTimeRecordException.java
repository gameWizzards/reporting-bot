package com.telegram.reporting.exception;

public class HandleTimeRecordException extends RuntimeException {

    public HandleTimeRecordException() {
    }

    public HandleTimeRecordException(String message) {
        super(message);
    }

    public HandleTimeRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
