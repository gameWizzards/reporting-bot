package com.telegram.reporting.dialogs.actions.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.dialogs.actions.GeneralActions;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeneralActionsImpl implements GeneralActions {
    private final SendBotMessageService sendBotMessageService;
    private final TimeRecordService timeRecordsService;
    private final DialogRouterService dialogRouterService;

    public GeneralActionsImpl(SendBotMessageService sendBotMessageService, TimeRecordService timeRecordsService,
                              DialogRouterService dialogRouterService) {
        this.sendBotMessageService = sendBotMessageService;
        this.timeRecordsService = timeRecordsService;
        this.dialogRouterService = dialogRouterService;
    }

    @Override
    public <S, E> void generalRequestInputDate(StateContext<S, E> context) {
        SendMessage sendMessage = new SendMessage(TelegramUtils.currentChatIdString(context), Message.USER_DATE_INPUT_GENERAL.text());
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createMainMenuButtonMarkup());
    }

    @Override
    public <S, E> void sendListTimeRecords(StateContext<S, E> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        Long chatId = TelegramUtils.currentChatId(context);
        String date = (String) variables.get(ContextVariable.DATE);

        List<TimeRecordTO> trTOs = timeRecordsService.getTimeRecordTOs(date, chatId);

        if (CollectionUtils.isEmpty(trTOs)) {
            String message = """
                    ?????? ?????????????? ???? - %s.
                    ???????????? ???????????? ???????? ?????? ?????????????? ?? ?????????????? ????????.
                    """.formatted(date);
            SendMessage sendMessage = new SendMessage(chatId.toString(), message);
            KeyboardRow rowButtons = KeyboardUtils.createRowButtons(ButtonValue.INPUT_NEW_DATE.text());
            sendBotMessageService.sendMessageWithKeys(sendMessage,
                    KeyboardUtils.createKeyboardMarkup(true, rowButtons));
            return;
        }

        String timeRecordMessage = MessageConvertorUtils.convertToListTimeRecordsMessage(trTOs);
        String[] buttons = KeyboardUtils.getButtonsByOrdinalNumber(trTOs);

        String message = """
                ?????? ?????????????????? ???????????? ???? - %s.
                                
                 ????????????: \n
                 %s
                                
                ???????????????? ?????????? ???? ????????????.
                """.formatted(date, timeRecordMessage);

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);
        variables.put(ContextVariable.TIME_RECORDS_JSON, timeRecordsJson);

        SendMessage sendMessage = new SendMessage(chatId.toString(), message);
        KeyboardRow[] rowButtons = KeyboardUtils.createButtonsWithRows(buttons, 10);

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(true, rowButtons));
    }

    @Override
    public <S, E> void handleChoiceTimeRecord(StateContext<S, E> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        Long ordinalNumberTR = Long.parseLong((String) variables.get(ContextVariable.TIME_RECORD_CHOICE));

        String timeRecordJson = (String) variables.get(ContextVariable.TIME_RECORDS_JSON);
        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);

        TimeRecordTO timeRecordTO = trTOS.stream()
                .filter(tr -> tr.getOrdinalNumber().equals(ordinalNumberTR))
                .findFirst()
                .orElse(null);

        String json = timeRecordTO != null ? JsonUtils.serializeItem(timeRecordTO) : "";
        variables.put(ContextVariable.TARGET_TIME_RECORD_JSON, json);
    }

    @Override
    public <S, E> void handleUserDateInput(StateContext<S, E> context) {
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.DATE);

        LocalDate reportDate = DateTimeUtils.convertUserInputToDate(userInput);

        String formattedReportDate = DateTimeUtils.toDefaultFormat(reportDate);
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "???????? ?????????????? = %s".formatted(formattedReportDate));
        context.getExtendedState().getVariables().put(ContextVariable.DATE, formattedReportDate);
    }

    @Override
    public <S, E> void handleUserMonthInput(StateContext<S, E> context) {
        final LocalDate localDate = LocalDate.now();
        final int defaultDay = 1;
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.DATE);
        //handle user input to month
        Integer[] parsedDate = parseUserInput(userInput);
        LocalDate reportDate = switch (parsedDate.length) {
            case 1 -> LocalDate.of(localDate.getYear(), parsedDate[0], defaultDay);
            case 2 -> LocalDate.of(parsedDate[1], parsedDate[0], defaultDay);
            default -> localDate;
        };

        String formattedReportDate = DateTimeUtils.toDefaultFormat(reportDate);
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "???????? ?????????????? = %s".formatted(formattedReportDate.substring(3)));
        context.getExtendedState().getVariables().put(ContextVariable.DATE, formattedReportDate);
    }

    @Override
    public <S, E> void handleUserTimeInput(StateContext<S, E> context) {
        String userInput = (String) context.getExtendedState().getVariables().get(ContextVariable.REPORT_TIME);

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "?????????? ?????????????? = %s ??.".formatted(userInput));
    }

    @Override
    public <S, E> void handleCategory(StateContext<S, E> context) {
        // TODO create method getContextVariable(ContextVariable var) in TGUtils
        String reportCategoryType = (String) context.getExtendedState().getVariables().get(ContextVariable.BUTTON_VALUE);
        context.getExtendedState().getVariables().put(ContextVariable.REPORT_CATEGORY_TYPE, reportCategoryType);
    }

    @Override
    public <S, E> void handleUserNoteInput(StateContext<S, E> context) {
        String note = "NA";
        String userMessage;
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String userInput = (String) variables.get(ContextVariable.REPORT_NOTE);
        String lastButtonText = (String) variables.get(ContextVariable.BUTTON_VALUE);
        boolean isSkipNote = ButtonValue.SKIP_NOTE.text().equals(lastButtonText.trim());

        if (isSkipNote) {
            userMessage = "?????????? ???????????? ?????? ????????????????????";
            variables.put(ContextVariable.REPORT_NOTE, note);
        } else {
            userMessage = "???????????????????? ?????????????? = \"%s\"".formatted(userInput);
        }

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), userMessage);
    }

    @Override
    public <S, E> void sendRootMenuButtons(StateContext<S, E> context) {
        Long chatId = TelegramUtils.currentChatId(context);
        dialogRouterService.startFlow(chatId);
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
