package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.Message;
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
public class HandleUserTimeInputAction implements Action<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public HandleUserTimeInputAction(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_TIME.name());

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), List.of(String.format("Время принято = %s ч.", userInput), Message.SEPARATOR.text()));

        // сохранить дату в контекст
        context.getExtendedState().getVariables().put(ContextVariable.REPORT_TIME.name(), userInput);
    }
}
