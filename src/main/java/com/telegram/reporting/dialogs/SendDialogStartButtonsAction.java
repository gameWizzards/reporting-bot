package com.telegram.reporting.dialogs;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendDialogStartButtonsAction implements Action<CreateReportState, MessageEvent> {

    private final SendBotMessageService sendBotMessageService;

    public SendDialogStartButtonsAction(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String chatId = TelegramUtils.currentChatId(context);
        sendBotMessageService.sendMessageWithKeys(KeyboardUtils.createRootMenuMessage(chatId));
    }
}
