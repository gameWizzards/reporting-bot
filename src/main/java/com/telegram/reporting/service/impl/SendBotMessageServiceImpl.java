package com.telegram.reporting.service.impl;

import com.telegram.reporting.bot.ReportingTelegramBot;
import com.telegram.reporting.service.SendBotMessageService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Implementation of {@link SendBotMessageService} interface.
 */
@Service
public class SendBotMessageServiceImpl implements SendBotMessageService {

    private final ReportingTelegramBot reportingTelegramBot;

    @Autowired
    public SendBotMessageServiceImpl(ReportingTelegramBot reportingTelegramBot) {
        this.reportingTelegramBot = reportingTelegramBot;
    }

    @Override
    @SneakyThrows
    public void sendMessage(SendMessage message) {
        reportingTelegramBot.execute(message);
    }

    @Override
    @SneakyThrows
    public void sendMessage(Long chatId, String message) {
        if (isBlank(message)) return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.enableHtml(true);
        sendMessage.setText(message);

        reportingTelegramBot.execute(sendMessage);
    }

    @Override
    public void sendMessage(Long chatId, List<String> messages) {
        if (isEmpty(messages)) return;

        messages.forEach(m -> sendMessage(chatId, m));
    }
}