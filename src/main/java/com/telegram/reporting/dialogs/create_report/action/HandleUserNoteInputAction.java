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
import java.util.Map;

@Slf4j
@Component
public class HandleUserNoteInputAction implements Action<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public HandleUserNoteInputAction(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String note = "NA";
        String userMessage;
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String userInput = (String) variables.get(ContextVariable.REPORT_NOTE.name());
        String lastButtonText = (String) variables.get(ContextVariable.MESSAGE.name());

        if (Message.SKIP_NOTE.text().equals(lastButtonText)) {
            userMessage = "Вы создали отчет без примечания";
            variables.put(ContextVariable.REPORT_NOTE.name(), note);
        } else {
            userMessage = String.format("Примечание принято = \"%s\"", userInput);
        }

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), List.of(userMessage, Message.SEPARATOR.text()));
    }
}
