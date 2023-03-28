package com.telegram.reporting.exception;

import lombok.Getter;

public final class TelegramEventException extends RuntimeException {
    @Getter
    private Long chatId;

    private TelegramEventException() {
    }

    public TelegramEventException(Long chatId, String message) {
        super(message);
        this.chatId = chatId;
    }
}
