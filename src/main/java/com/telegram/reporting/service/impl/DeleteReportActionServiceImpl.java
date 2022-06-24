package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.delete_report.DeleteReportState;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.service.DeleteReportActionService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.utils.JsonUtils;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import com.telegram.reporting.utils.TimeRecordUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class DeleteReportActionServiceImpl implements DeleteReportActionService {
    private final SendBotMessageService sendBotMessageService;
    private final TimeRecordService timeRecordService;

    public DeleteReportActionServiceImpl(SendBotMessageService sendBotMessageService,
                                         TimeRecordService timeRecordService) {
        this.sendBotMessageService = sendBotMessageService;
        this.timeRecordService = timeRecordService;
    }

    @Override
    public void requestInputDate(StateContext<DeleteReportState, MessageEvent> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), Message.USER_DATE_INPUT_DELETE_REPORT.text());
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());
    }

    @Override
    public void requestDeleteConfirmation(StateContext<DeleteReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        String date = (String) variables.get(ContextVariable.DATE);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);

        String message = """
                Хочешь удалить отчет за - %s.
                                
                  %s
                      
                  %s
                """;

        TimeRecordTO trTO = JsonUtils.deserializeItem(timeRecordJson, TimeRecordTO.class);
        String timeRecordMessage = TimeRecordUtils.convertTimeRecordToMessage(trTO);

        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), String.format(message, date, timeRecordMessage, Message.REQUEST_DELETE_CONFIRMATION_REPORT.text()));
        KeyboardRow firstRow = KeyboardUtils.createRowButtons(ButtonValue.CONFIRM_DELETE_TIME_RECORD.text(), ButtonValue.CANCEL.text());
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, firstRow));
    }

    @Override
    public void removeTimeRecord(StateContext<DeleteReportState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        String chatId = TelegramUtils.currentChatId(context);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);

        if (StringUtils.isBlank(timeRecordJson)) {
            sendBotMessageService.sendMessage(chatId, "Что-то пошло не так. Отчет не удален(");
            throw new NoSuchElementException("Can't find timeRecord to delete on Date = " + variables.get(ContextVariable.DATE));
        }
        TimeRecordTO timeRecordTO = JsonUtils.deserializeItem(timeRecordJson, TimeRecordTO.class);
        timeRecordService.deleteByTimeRecordTO(timeRecordTO);

        log.info("{} timeRecord removed - {}", variables.get(ContextVariable.LOG_PREFIX), timeRecordTO);

        sendBotMessageService.sendMessage(chatId, "Отчет успешно удален!");
    }
}
