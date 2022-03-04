package com.telegram.reporting.dialogs.create_report.guard;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateTimeGuard implements Guard<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public ValidateTimeGuard(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public boolean evaluate(StateContext<CreateReportState, MessageEvent> context) {
        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_TIME.name());
        StateMachine<CreateReportState, MessageEvent> stateMachine = context.getStateMachine();

        if (userInput.matches("\\d+")) {
            stateMachine.sendEvent(MessageEvent.USER_DATE_INPUT_VALIDATE);
            return true;
        }

        sendBotMessageService.sendMessage(chatId, "Вы не верно ввели время. Допустимые значения - положительные числа");
        return false;
    }

}
