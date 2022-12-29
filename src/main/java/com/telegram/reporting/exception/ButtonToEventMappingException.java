package com.telegram.reporting.exception;

import lombok.Getter;

public class ButtonToEventMappingException extends RuntimeException {
    @Getter
    private final long chatId;

    public ButtonToEventMappingException(long chatId, String message) {
        super(message);
        this.chatId = chatId;
    }
}
