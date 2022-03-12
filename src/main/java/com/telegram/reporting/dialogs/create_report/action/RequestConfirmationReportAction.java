package com.telegram.reporting.dialogs.create_report.action;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.create_report.CreateReportState;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.messages.MessageEvent;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.JsonUtils;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestConfirmationReportAction implements Action<CreateReportState, MessageEvent> {
    private final SendBotMessageService sendBotMessageService;

    public RequestConfirmationReportAction(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(StateContext<CreateReportState, MessageEvent> context) {
        var message = """
                Вы хотите отправить отчет за - %s.
                
                 Отчеты: \n
                  %s
                
                  %s
                """;

        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String date = (String) variables.get(ContextVariable.REPORT_DATE);

        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);

        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);

        String timeRecordMessage = trTOS.stream()
                .map(this::convertTimeRecordToMessage)
                .collect(Collectors.joining("\n"));

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), String.format(message, date, timeRecordMessage, Message.REQUEST_CONFIRMATION_REPORT.text()));

        KeyboardRow firstRow = KeyboardUtils.createRowButtons(Message.CONFIRM_CREATION_FINAL_REPORT.text(), Message.DECLINE_CREATION_FINAL_REPORT.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(firstRow));
    }

    private String convertTimeRecordToMessage(TimeRecordTO timeRecordTO) {
        var timeRecordMessage = """
                Категория рабочего времени - "%s".
                Затраченное время - %s ч.
                Примечание - "%s."
                 """;
        return String.format(timeRecordMessage, timeRecordTO.getCategoryName(), timeRecordTO.getHours(), timeRecordTO.getNote());
    }
}
