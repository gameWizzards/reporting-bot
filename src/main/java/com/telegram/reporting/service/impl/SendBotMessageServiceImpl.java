package com.telegram.reporting.service.impl;

import com.telegram.reporting.bot.ReportingTelegramBot;
import com.telegram.reporting.service.SendBotMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class SendBotMessageServiceImpl implements SendBotMessageService {
    private final ReportingTelegramBot reportingTelegramBot;

    public SendBotMessageServiceImpl(@Lazy ReportingTelegramBot reportingTelegramBot) {
        this.reportingTelegramBot = reportingTelegramBot;
    }

    @Override
    public void sendMessageWithKeys(SendMessage message, ReplyKeyboard keyboardMarkup) {
        Validate.notNull(keyboardMarkup, "Keyboard markup is required to send it to telegramBot!");
        Validate.notNull(message, "SendMessage is required to send it to telegramBot!");
        message.setReplyMarkup(keyboardMarkup);
        message.enableHtml(true);
        sendMessage2Telegram(message);
    }

    public void sendMessageWithKeys(SendMessage message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        Validate.notNull(inlineKeyboardMarkup, "Keyboard markup is required to send it to telegramBot!");
        Validate.notNull(message, "SendMessage is required to send it to telegramBot!");
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.enableHtml(true);
        sendMessage2Telegram(message);
    }

    @Override
    public void sendMessage(Long chatId, String message) {
        Validate.notNull(chatId, "ChatId is required to send message to telegramBot!");
        Validate.notBlank(message, "Message is required to send it to telegramBot!");
        sendMessage(chatId.toString(), message);
    }

    @Override
    public void sendMessage(String chatId, String message) {
        Validate.notNull(chatId, "ChatId is required to send message to telegramBot!");
        Validate.notBlank(message, "Message is required to send it to telegramBot!");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));

        sendMessage2Telegram(sendMessage);
    }


    @Override
    public void removeReplyKeyboard(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), message);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sendMessage2Telegram(sendMessage);
    }

    private void sendMessage2Telegram(SendMessage message) {
        try {
            reportingTelegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Can't send message to telegram API. Message: {}. Reason: {}", message, e.getMessage(), e);
        }
    }

}
