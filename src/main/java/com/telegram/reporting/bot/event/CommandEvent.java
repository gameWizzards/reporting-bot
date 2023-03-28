package com.telegram.reporting.bot.event;

public record CommandEvent(Long chatId, String command, FromUser fromUser) implements TelegramEvent {

    public record FromUser(String name, String surname, String nickName, Boolean isBot) {
    }
}
