package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.service.GeneralActionService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
    private final TimeRecordService timeRecordsService;

    public GeneralActionServiceImpl(SendBotMessageService sendBotMessageService,
                                    TimeRecordService timeRecordsService) {
        this.sendBotMessageService = sendBotMessageService;
        this.timeRecordsService = timeRecordsService;
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
        KeyboardRow row = KeyboardUtils.createRowButtons(ButtonValue.SKIP_NOTE.text());

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, row));

    }

    @Override
    public <S, E> void handleUserNoteInput(StateContext<S, E> context) {
        String note = "NA";
        String userMessage;
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String userInput = (String) variables.get(ContextVariable.REPORT_NOTE);
        String lastButtonText = (String) variables.get(ContextVariable.MESSAGE);
        boolean isSkipNote = ButtonValue.SKIP_NOTE.text().equals(lastButtonText.trim());

        if (isSkipNote) {
            userMessage = "Отчет создан без примечания";
            variables.put(ContextVariable.REPORT_NOTE, note);
        } else {
            userMessage = String.format("Примечание принято = \"%s\"", userInput);
        }

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), userMessage);
    }

    //TODO make check to limit of timeRecord for one report with AOP
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
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Отправка отчета отменена!");
    }

    @Override
    public <S, E> void sendListTimeRecords(StateContext<S, E> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        String chatId = TelegramUtils.currentChatId(context);
        String date = (String) variables.get(ContextVariable.DATE);

        List<TimeRecordTO> trTOs = timeRecordsService.getTimeRecordTOs(date, chatId);

        if (CollectionUtils.isEmpty(trTOs)) {
            String message = """
                    Нет отчетов за - %s.
                    Выбери другую дату или
                    вернись в главное меню.
                    """;
            SendMessage sendMessage = new SendMessage(chatId, String.format(message, date));
            KeyboardRow rowButtons = KeyboardUtils.createRowButtons(ButtonValue.INPUT_NEW_DATE.text());
            sendBotMessageService.sendMessageWithKeys(sendMessage,
                    KeyboardUtils.createKeyboardMarkup(true, rowButtons));
            return;
        }

        Long ordinalNumber = 1L;
        List<String> buttons = new ArrayList<>();
        StringBuilder timeRecordMessage = new StringBuilder();

        String message = """
                Вот доступные отчеты за - %s.
                                
                 Отчеты: \n
                  %s
                                
                Выберите отчет для удаления.
                """;

        for (TimeRecordTO timeRecordTO : trTOs) {
            timeRecordTO.setOrdinalNumber(ordinalNumber);
            String trMessage = TimeRecordUtils.convertTimeRecordToMessage(timeRecordTO);
            timeRecordMessage.append(ordinalNumber)
                    .append(". ")
                    .append(trMessage)
                    .append("\n");
            buttons.add(String.valueOf(ordinalNumber));
            ordinalNumber++;
        }

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);
        variables.put(ContextVariable.TIME_RECORDS_JSON, timeRecordsJson);

        SendMessage sendMessage = new SendMessage(chatId, String.format(message, date, timeRecordMessage));
        KeyboardRow rowButtons = KeyboardUtils.createRowButtons(buttons.toArray(new String[0]));

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, rowButtons));
    }

    @Override
    public <S, E> void handleTimeRecord(StateContext<S, E> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        Long ordinalNumberTR = Long.parseLong((String) variables.get(ContextVariable.TIME_RECORD_CHOICE));

        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);
        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);

        TimeRecordTO timeRecordTO = trTOS.stream()
                .filter(tr -> tr.getOrdinalNumber().equals(ordinalNumberTR))
                .findFirst()
                .orElse(null);

        String json = timeRecordTO != null ? JsonUtils.serializeItem(timeRecordTO) : "";
        variables.put(ContextVariable.TIME_RECORDS_JSON, json);
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
