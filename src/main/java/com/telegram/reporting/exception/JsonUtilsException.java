package com.telegram.reporting.exception;

public class JsonUtilsException extends RuntimeException {

    public JsonUtilsException() {
    }

    public JsonUtilsException(String message) {
        super(message);
    }

    public JsonUtilsException(String message, Throwable cause) {
        super(message, cause);
    }
}
