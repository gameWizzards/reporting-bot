package com.telegram.reporting.bot.event;

public record SendContactEvent(Long chatId, String normalizedPhoneNumber, String name, String surname, String nickName) implements TelegramEvent {

}
