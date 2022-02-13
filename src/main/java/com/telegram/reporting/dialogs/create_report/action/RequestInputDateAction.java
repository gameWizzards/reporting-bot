package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@Component
public class RequestInputDateAction implements Action<CreateReportState, MessageEvent> {
    @Autowired
    private SendBotMessageService sendBotMessageService;

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String chatId = TelegramUtils.currentChatId(context);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Введите дату для создния отчета");
        sendBotMessageService.sendMessage(message);
        log.info("User, please input the valid date");
    }
}
