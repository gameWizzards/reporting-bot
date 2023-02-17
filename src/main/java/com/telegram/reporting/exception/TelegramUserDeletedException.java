package com.telegram.reporting.exception;

import lombok.Getter;

public final class TelegramUserDeletedException extends RuntimeException {
    @Getter
    private Long chatId;

    private TelegramUserDeletedException() {
    }

    public TelegramUserDeletedException(Long chatId, String message) {
        super(message);
        this.chatId = chatId;
    }
}
