package com.telegram.reporting.exception;

public class TelegramUserException extends RuntimeException {
    public TelegramUserException() {
    }

    public TelegramUserException(String message) {
        super(message);
    }

    public TelegramUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
