package com.telegram.reporting.service;

import com.telegram.reporting.job.bot.ReportingTelegramBot;
import com.telegram.reporting.service.impl.SendBotMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@DisplayName("Unit-level testing for SendBotMessageService")
public class SendBotMessageServiceTest {

    private SendBotMessageService sendBotMessageService;
    private ReportingTelegramBot reportingTelegramBot;

    @BeforeEach
    public void init() {
        reportingTelegramBot = Mockito.mock(ReportingTelegramBot.class);
        sendBotMessageService = new SendBotMessageServiceImpl(reportingTelegramBot);
    }

    @Test
    public void shouldProperlySendMessage() throws TelegramApiException {
        //given
        Long chatId = 123L;
        String message = "test_message";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId.toString());
        sendMessage.enableHtml(true);

        //when
        sendBotMessageService.sendMessage(chatId, message);

        //then
        Mockito.verify(reportingTelegramBot).execute(sendMessage);
    }
}
