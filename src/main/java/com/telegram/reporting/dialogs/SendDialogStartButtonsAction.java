package com.telegram.reporting.dialogs;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Slf4j
@Component
public class SendDialogStartButtonsAction implements Action<CreateReportState, MessageEvent> {
    public final static String START_FLOW_MESSAGE = """
            Окей.
            Выбери диалог.
            """;
    private final SendBotMessageService sendBotMessageService;

    public SendDialogStartButtonsAction(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), START_FLOW_MESSAGE);

        KeyboardRow firstRow = KeyboardUtils.createButton(Message.CREATE_REPORT.text());
        KeyboardRow secondRow = KeyboardUtils.createRowButtons(Message.UPDATE_REPORT.text(), Message.DELETE_REPORT.text());
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(firstRow, secondRow));

    }
}
