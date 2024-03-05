package com.telegram.reporting.dialogs;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.dto.TimeRecordTO;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.RuntimeDialogManager;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.DateTimeUtils;
import com.telegram.reporting.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonActions {
    private final SendBotMessageService sendBotMessageService;
    private final TimeRecordService timeRecordsService;
    private final DialogRouterService dialogRouterService;
    private final RuntimeDialogManager runtimeDialogManager;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18NMessageService;


    public <S, E> void requestInputDate(StateContext<S, E> context) {
        Long chatId = CommonUtils.currentChatId(context);
        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_REQUEST_INPUT_DATE));
        sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createMainMenuInlineMarkup(chatId));
    }

    public <S, E> void sendListTimeRecords(StateContext<S, E> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);

        List<TimeRecordTO> trTOs = timeRecordsService.getTimeRecordTOs(date, chatId);

        if (CollectionUtils.isEmpty(trTOs)) {
            String message = i18NMessageService.getMessage(chatId, MessageKey.COMMON_REPORT_ABSENT, date);

            SendMessage sendMessage = new SendMessage(chatId.toString(), message);

            ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU, ButtonLabelKey.COMMON_INPUT_NEW_DATE);
            sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
            return;
        }

        String timeRecordMessage = i18NMessageService.convertToListTimeRecordsMessage(chatId, trTOs);
        String message = i18NMessageService.getMessage(chatId, MessageKey.COMMON_CHOOSE_AVAILABLE_REPORTS, date, timeRecordMessage);

        SendMessage sendMessage = new SendMessage(chatId.toString(), message);
        ReplyKeyboard inlineMarkup = i18nButtonService.createOrdinalButtonsInlineMarkup(
                chatId, MenuButtons.MAIN_MENU, trTOs, 10);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);
        context.getExtendedState().getVariables().put(ContextVarKey.TIME_RECORDS_JSON, timeRecordsJson);
    }

    public <S, E> void handleChoiceTimeRecord(StateContext<S, E> context) {
        Long ordinalNumberTR = CommonUtils.getContextVar(context, Long.class, ContextVarKey.TIME_RECORD_CHOICE);

        String timeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TIME_RECORDS_JSON);
        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);

        TimeRecordTO timeRecordTO = trTOS.stream()
                .filter(tr -> tr.getOrdinalNumber().equals(ordinalNumberTR))
                .findFirst()
                .orElse(null);

        String json = Objects.nonNull(timeRecordTO) ? JsonUtils.serializeItem(timeRecordTO) : "";
        context.getExtendedState().getVariables().put(ContextVarKey.TARGET_TIME_RECORD_JSON, json);
    }

    public <S, E> void handleUserDateInput(StateContext<S, E> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);

        LocalDate reportDate = DateTimeUtils.parseShortDateToLocalDate(userInput);

        String formattedReportDate = DateTimeUtils.toDefaultFormat(reportDate);
        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_DATE_ACCEPTED, formattedReportDate));

        context.getExtendedState().getVariables().put(ContextVarKey.DATE, formattedReportDate);
    }

    public <S, E> void handleUserMonthInput(StateContext<S, E> context) {
        Long chatId = CommonUtils.currentChatId(context);
        final LocalDate localDate = LocalDate.now();
        final int defaultDay = 1;
        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);
        //handle user input to month
        Integer[] parsedDate = parseUserInput(userInput);
        LocalDate reportDate = switch (parsedDate.length) {
            case 1 -> LocalDate.of(localDate.getYear(), parsedDate[0], defaultDay);
            case 2 -> LocalDate.of(parsedDate[1], parsedDate[0], defaultDay);
            default -> localDate;
        };

        String formattedReportDate = DateTimeUtils.toDefaultFormat(reportDate);

        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_DATE_ACCEPTED, formattedReportDate.substring(3)));

        context.getExtendedState().getVariables().put(ContextVarKey.DATE, formattedReportDate);
    }

    public <S, E> void handleUserTimeInput(StateContext<S, E> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_TIME);

        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_TIME_ACCEPTED, userInput));
    }

    public <S, E> void handleCategory(StateContext<S, E> context) {
        String reportCategoryType = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);
        context.getExtendedState().getVariables().put(ContextVarKey.REPORT_CATEGORY_TYPE_KEY, reportCategoryType);
    }

    public <S, E> void handleUserNoteInput(StateContext<S, E> context) {
        String note = "NA";
        String userMessage;
        Long chatId = CommonUtils.currentChatId(context);
        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_NOTE);
        String lastButtonText = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);
        boolean isSkipNote = ButtonLabelKey.GCR_SKIP_NOTE.value().equals(lastButtonText.trim());

        if (isSkipNote) {
            userMessage = i18NMessageService.getMessage(chatId, MessageKey.COMMON_REPORT_WITHOUT_NOTE);
            context.getExtendedState().getVariables().put(ContextVarKey.REPORT_NOTE, note);
        } else {
            userMessage = i18NMessageService.getMessage(chatId, MessageKey.COMMON_NOTE_ACCEPTED, userInput);
        }

        sendBotMessageService.sendMessage(chatId, userMessage);
    }

    public <S, E> void startRootMenuFlow(StateContext<S, E> context) {
        Long chatId = CommonUtils.currentChatId(context);
        User principalUser = runtimeDialogManager.getPrincipalUser(chatId);
        dialogRouterService.startFlow(chatId, principalUser.getLocale());
    }

    private Integer[] parseUserInput(String userInput) {
        String[] date = userInput
                .trim()
                .replaceAll("\\D+", "-")
                .split("-");

        return Arrays.stream(date)
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
    }
}
