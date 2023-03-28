package com.telegram.reporting.bot.event;

public record UserInputEvent(Long chatId, String userInput) implements TelegramEvent {
}
