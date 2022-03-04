package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EndDialogAction implements Action<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public EndDialogAction(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
//        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), List.of("Вы успешно создали отчет", "All rights reserved", "Cyberdyne Systems"));
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), List.of("Конец презентации", "All rights reserved", "Cyberdyne Systems"));
    }
}
