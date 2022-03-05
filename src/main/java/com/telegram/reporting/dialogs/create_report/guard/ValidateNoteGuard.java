package com.telegram.reporting.dialogs.create_report.guard;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateNoteGuard implements Guard<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public ValidateNoteGuard(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public boolean evaluate(StateContext<CreateReportState, MessageEvent> context) {
        int minNoteLength = 5;
        String chatId = TelegramUtils.currentChatId(context);
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_NOTE.name());
        String lastButtonText = (String) context.getExtendedState().getVariables().get(ContextVariable.MESSAGE.name());
        boolean isSkipNote = Message.SKIP_NOTE.text().equals(lastButtonText.trim());

        if (isSkipNote || (userInput != null && userInput.trim().length() >= minNoteLength)) {
            return true;
        }

        sendBotMessageService.sendMessage(chatId, "Ваше примечание слишком лаконичное)) попробуйте написать больше БУКАВ)) Минимальное количество - " + minNoteLength);
        return false;
    }
}
