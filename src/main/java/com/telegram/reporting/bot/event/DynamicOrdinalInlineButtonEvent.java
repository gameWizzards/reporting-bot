package com.telegram.reporting.bot.event;

public record DynamicOrdinalInlineButtonEvent(Long chatId, String buttonCallbackData) implements TelegramEvent {
}
