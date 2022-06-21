package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.messages.Message;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.service.GeneralActionService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.DateTimeUtils;
import com.telegram.reporting.utils.JsonUtils;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeneralActionServiceImpl implements GeneralActionService {
    private final SendBotMessageService sendBotMessageService;

    public GeneralActionServiceImpl(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public <S, E> void sendRootMenuButtons(StateContext<S, E> context) {
        String chatId = TelegramUtils.currentChatId(context);
        sendBotMessageService.sendMessageWithKeys(KeyboardUtils.createRootMenuMessage(chatId));
    }

    @Override
    public <S, E> void handleUserDateInput(StateContext<S, E> context) {
        final LocalDate localDate = LocalDate.now();
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.DATE);
        //handle user input to date
        Integer[] parsedDate = parseUserInput(userInput);
        LocalDate reportDate = switch (parsedDate.length) {
            case 1 -> LocalDate.of(localDate.getYear(), localDate.getMonth(), parsedDate[0]);
            case 2 -> LocalDate.of(localDate.getYear(), parsedDate[1], parsedDate[0]);
            case 3 -> LocalDate.of(parsedDate[2], parsedDate[1], parsedDate[0]);
            default -> localDate;
        };

        String formattedReportDate = DateTimeUtils.toDefaultFormat(reportDate);
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Дата принята = " + formattedReportDate);
        // сохранить дату в контекст
        context.getExtendedState().getVariables().put(ContextVariable.DATE, formattedReportDate);
    }

    @Override
    public <S, E> void handleUserTimeInput(StateContext<S, E> context) {
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_TIME);

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), String.format("Время принято = %s ч.", userInput));
    }

    @Override
    public <S, E> void requestInputNote(StateContext<S, E> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatId(context), Message.REQUEST_ADD_NOTE_REPORT.text());
        KeyboardRow row = KeyboardUtils.createRowButtons(Message.SKIP_NOTE.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, row));

    }

    @Override
    public <S, E> void handleUserNoteInput(StateContext<S, E> context) {
        String note = "NA";
        String userMessage;
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String userInput = (String) variables.get(ContextVariable.REPORT_NOTE);
        String lastButtonText = (String) variables.get(ContextVariable.MESSAGE);

        if (Message.SKIP_NOTE.text().equals(lastButtonText)) {
            userMessage = "Вы создали отчет без примечания";
            variables.put(ContextVariable.REPORT_NOTE, note);
        } else {
            userMessage = String.format("Примечание принято = \"%s\"", userInput);
        }

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), userMessage);
    }

    @Override
    public <S, E> void prepareTimeRecord(StateContext<S, E> context) {
        List<TimeRecordTO> trTOs;
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String time = (String) variables.get(ContextVariable.REPORT_TIME);
        String note = (String) variables.get(ContextVariable.REPORT_NOTE);
        String categoryName = (String) variables.get(ContextVariable.REPORT_CATEGORY_TYPE);
        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);

        trTOs = StringUtils.isNotBlank(timeRecordJson)
                ? JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class)
                : new ArrayList<>();

        TimeRecordTO timeRecord = new TimeRecordTO();
        timeRecord.setHours(Integer.parseInt(time));
        timeRecord.setNote(note);
        timeRecord.setCategoryName(categoryName);
        timeRecord.setCreated(LocalDateTime.now());

        trTOs.add(timeRecord);

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);

        variables.put(ContextVariable.TIME_RECORDS_JSON, timeRecordsJson);

    }

    @Override
    public <S, E> void declinePersistReport(StateContext<S, E> context) {
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Вы отменили отправку отчета.");
    }


    private Integer[] parseUserInput(String userInput) {
        String[] date = userInput
                .replaceAll("\\D+", "-")
                .split("-");

        return Arrays.stream(date)
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
    }
}
