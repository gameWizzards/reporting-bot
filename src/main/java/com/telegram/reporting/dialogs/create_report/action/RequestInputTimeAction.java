package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.ContextVariable;
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

@Slf4j
@Component
public class RequestInputTimeAction implements Action<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public RequestInputTimeAction(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        String reportCategoryType = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_CATEGORY_TYPE);

        String userMessageCategoryAccepted = String.format("""
                        Вы выбрали категорию отчета - "%s". Категория принята.
                                        
                        %s
                        """,
                reportCategoryType,
                Message.USER_TIME_INPUT.text());

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), userMessageCategoryAccepted);

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());

    }
}
