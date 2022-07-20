package com.telegram.reporting.service.impl;

import com.telegram.reporting.bot.ReportingTelegramBot;
import com.telegram.reporting.repository.dto.EmployeeTO;
import com.telegram.reporting.service.SendBotMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class SendBotMessageServiceImpl implements SendBotMessageService {
    private final String USER_CHAT_LINK = "<a href=\"https://t.me/+%s\">\"Перейти в чат\"</a>";


    private final ReportingTelegramBot reportingTelegramBot;

    public SendBotMessageServiceImpl(ReportingTelegramBot reportingTelegramBot) {
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
    public void sendLink2UserChat(Long chatId, EmployeeTO employee) {
        Validate.notNull(chatId, "ChatId is required to send message to telegramBot!");
        Validate.notNull(employee, "Employee is required to create link to his chat!");

        String userName = employee.getFullName();
        if (StringUtils.isBlank(userName)) {
            userName = "телеграм бота";
        }

        String link = USER_CHAT_LINK.formatted(employee.getPhone());
        String message = "Кликни ссылку %s чтобы написать пользователю %s".formatted(link, userName);
        sendMessage(chatId, message);
    }

    private void sendMessage2Telegram(SendMessage message) {
        try {
            reportingTelegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Can't send message to telegram API. Message: {}. Reason: {}", message, e.getMessage(), e);
        }
    }

}
