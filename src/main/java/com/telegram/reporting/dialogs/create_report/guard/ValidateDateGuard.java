package com.telegram.reporting.dialogs.create_report.guard;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ValidateDateGuard implements Guard<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public ValidateDateGuard(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public boolean evaluate(StateContext<CreateReportState, MessageEvent> context) {
        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_DATE.name());

        if (userInput.matches(".*\\d+.*")) {
            context.getStateMachine().sendEvent(MessageEvent.USER_DATE_INPUT_VALIDATE);
            return true;
        }

        sendBotMessageService.sendMessage(chatId, "Вы не верно ввели дату. Попробуйте еще раз в формате - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return false;
    }

}
