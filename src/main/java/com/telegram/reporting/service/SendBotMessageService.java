package com.telegram.reporting.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

/**
 * Service for sending messages via telegram-bot.
 */
public interface SendBotMessageService {

    void sendMessageWithKeys(SendMessage message);

    void sendMessageWithKeys(SendMessage message, ReplyKeyboardMarkup keyboardMarkup);

    /**
     * Send message via telegram bot.
     *
     * @param chatId  provided chatId in which would be sent.
     * @param message provided message to be sent.
     */
    void sendMessage(Long chatId, String message);

    void sendMessage(String chatId, String message);

    /**
     * Send messages via telegram bot.
     *
     * @param chatId  provided chatId in which would be sent.
     * @param message collection of provided messages to be sent.
     */
    void sendMessage(Long chatId, List<String> message);

    void sendMessage(String chatId, List<String> message);

}
