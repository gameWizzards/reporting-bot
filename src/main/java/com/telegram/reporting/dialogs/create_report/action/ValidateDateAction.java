package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Slf4j
@Component
public class ValidateDateAction implements Action<CreateReportState, MessageEvent> {
    @Autowired
    private SendBotMessageService sendBotMessageService;

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String userInput = (String) context.getExtendedState().getVariables().get(MessageEvent.USER_DATE_INPUT.name());
        StateMachine<CreateReportState, MessageEvent> stateMachine = context.getStateMachine();
        if(validateDate(userInput)){
            stateMachine.sendEvent(MessageEvent.VALID_DATE);
            sendMessageToBot(TelegramUtils.currentChatId(context));
        } else {
            stateMachine.sendEvent(MessageEvent.INVALID_DATE);
        }
    }

    private void sendMessageToBot(String chatId) {

        SendMessage sendMessage = new SendMessage(chatId,"Выберете категорию отчета");
        KeyboardRow firstRow = KeyboardUtils.createRowButtons("На складе", "На заказе");
        KeyboardRow secondRow = KeyboardUtils.createRowButtons("На офисе", "На координации");
        sendMessage.setReplyMarkup(KeyboardUtils.createKeyboardMarkup(firstRow, secondRow));
        sendBotMessageService.sendMessage(sendMessage);
    }

    private boolean validateDate(String userInput) {
        if (userInput == null) {
            return false;
        }
        return true;
    }
}
