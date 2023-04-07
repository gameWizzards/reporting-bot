package com.telegram.reporting.dialogs.general_dialogs.create_report;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.CacheService;
import com.telegram.reporting.service.CategoryService;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.RuntimeDialogManager;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.DateTimeUtils;
import com.telegram.reporting.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateReportActions {
    private final SendBotMessageService sendBotMessageService;
    private final RuntimeDialogManager runtimeDialogManager;
    private final ReportService reportService;
    private final TimeRecordService timeRecordService;
    private final I18nMessageService i18NMessageService;
    private final I18nButtonService i18nButtonService;
    private final CategoryService categoryService;
    private final CacheService cacheService;

    public void requestInputDate(StateContext<CreateReportState, CreateReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);

        SendMessage sendMessage = new SendMessage(
                chatId.toString(),
                i18NMessageService.getMessage(chatId, MessageKey.GCR_INPUT_DATE_TO_CREATE_REPORT));

        sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createMainMenuInlineMarkup(chatId));
    }

    public void sendExistedTimeRecords(StateContext<CreateReportState, CreateReportEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        Long chatId = CommonUtils.currentChatId(context);
        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);

        List<TimeRecordTO> trTOs = timeRecordService.getTimeRecordTOs(date, chatId);

        if (CollectionUtils.isEmpty(trTOs)) {
            variables.put(ContextVarKey.TIME_RECORDS_JSON, "");
            return;
        }

        String timeRecordMessage = i18NMessageService.convertToListTimeRecordsMessage(chatId, trTOs);

        String message = i18NMessageService.getMessage(
                chatId,
                MessageKey.GCR_PREVIOUSLY_CREATED_REPORTS,
                date,
                timeRecordMessage);

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);
        variables.put(ContextVarKey.TIME_RECORDS_JSON, timeRecordsJson);

        sendBotMessageService.sendMessage(chatId, message);
    }

    public void sendCategoryButtons(StateContext<CreateReportState, CreateReportEvent> context) {
        SendMessage sendMessage;
        String message;

        Long chatId = CommonUtils.currentChatId(context);
        String timeRecordsJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TIME_RECORDS_JSON);

        List<List<InlineKeyboardButton>> rows = i18nButtonService.getAvailableCategoryInlineButtons(chatId, timeRecordsJson, 2);

        boolean isAllCategoriesOccupied = StringUtils.isNotBlank(timeRecordsJson) && rows.isEmpty();

        if (isAllCategoriesOccupied) {
            CreateReportState currentState = context.getSource().getId();
            switch (currentState) {
                case USER_DATE_INPUTTING -> {
                    message = i18NMessageService.getMessage(chatId, MessageKey.GCR_ALL_CATEGORIES_OCCUPIED);

                    sendMessage = new SendMessage(chatId.toString(), message);

                    ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU, ButtonLabelKey.COMMON_INPUT_NEW_DATE);
                    sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
                }
                case USER_CREATE_ADDITIONAL_REPORT -> {
                    message = i18NMessageService.getMessage(chatId, MessageKey.GCR_ALL_CATEGORIES_COMPLETED);

                    sendMessage = new SendMessage(chatId.toString(), message);

                    ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU, ButtonLabelKey.COMMON_LIST_TIME_RECORDS);
                    sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
                }
            }
            return;
        }

        String chooseCategoryMessage = i18NMessageService.getMessage(chatId, MessageKey.COMMON_CHOOSE_CATEGORY);

        List<Category> categories = categoryService.getAll(false);

        String categoryDescription = categories.stream()
                .map(Category::getDescriptionKey)
                .map(labelKey -> i18NMessageService.getMessage(chatId, labelKey))
                .map(desc -> "* " + desc)
                .collect(Collectors.joining("\n"));

        sendMessage = new SendMessage(chatId.toString(), String.join("\n", chooseCategoryMessage, categoryDescription));

        sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createInlineMarkup(chatId, MenuButtons.MAIN_MENU, rows));
    }

    public void requestInputTime(StateContext<CreateReportState, CreateReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String reportCategoryTypeKey = CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_CATEGORY_TYPE_KEY);
        String localizedReportCategory = i18NMessageService.getMessage(chatId, reportCategoryTypeKey);

        String userCategoryAcceptedMessage = i18NMessageService.getMessage(
                chatId,
                MessageKey.GCR_DISPLAY_CHOSEN_CATEGORY,
                localizedReportCategory);

        String inputSpendTimeMessage = i18NMessageService.getMessage(
                chatId,
                MessageKey.GCR_INPUT_SPEND_TIME);

        SendMessage sendMessage = new SendMessage(chatId.toString(),
                String.join("\n\n", userCategoryAcceptedMessage, inputSpendTimeMessage));

        sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createMainMenuInlineMarkup(chatId));
    }

    public void requestInputNote(StateContext<CreateReportState, CreateReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);

        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18NMessageService.getMessage(chatId, MessageKey.GCR_REQUEST_ADD_NOTE));

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU, ButtonLabelKey.GCR_SKIP_NOTE);
        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    //TODO make check to limit of timeRecord for one report with AOP
    public void prepareTimeRecord(StateContext<CreateReportState, CreateReportEvent> context) {
        List<TimeRecordTO> trTOs;

        String time = CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_TIME);
        String note = CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_NOTE);
        String categoryNameKey = CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_CATEGORY_TYPE_KEY);
        String timeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TIME_RECORDS_JSON);

        trTOs = StringUtils.isNotBlank(timeRecordJson)
                ? JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class)
                : new ArrayList<>();

        TimeRecordTO timeRecord = new TimeRecordTO();
        timeRecord.setHours(Integer.parseInt(time));
        timeRecord.setNote(note);
        timeRecord.setCategoryNameKey(categoryNameKey);
        timeRecord.setCreated(LocalDateTime.now());

        trTOs.add(timeRecord);

        String timeRecordsJson = JsonUtils.serializeItem(trTOs);

        context.getExtendedState().getVariables().put(ContextVarKey.TIME_RECORDS_JSON, timeRecordsJson);
    }

    public void requestAdditionalReport(StateContext<CreateReportState, CreateReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);

        SendMessage sendMessage = new SendMessage(chatId.toString(), i18NMessageService.getMessage(chatId, MessageKey.GCR_REQUEST_ADDITIONAL_REPORT));
        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU, ButtonLabelKey.COMMON_YES, ButtonLabelKey.COMMON_NO);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    public void requestConfirmationReport(StateContext<CreateReportState, CreateReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);
        String timeRecordJson = CommonUtils.getContextVarAsString(context, (ContextVarKey.TIME_RECORDS_JSON));

        List<TimeRecordTO> trTOs = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);

        String timeRecordMessage = i18NMessageService.convertToListTimeRecordsMessage(chatId, trTOs);

        String reportsMessage = i18NMessageService.getMessage(chatId, MessageKey.GCR_REQUEST_SEND_REPORT, date, timeRecordMessage);

        String confirmationSendMessage = i18NMessageService.getMessage(chatId, MessageKey.GCR_REQUEST_CONFIRMATION_SEND_REPORT);

        SendMessage sendMessage = new SendMessage(
                chatId.toString(),
                String.join("\n", reportsMessage, confirmationSendMessage));

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU,
                ButtonLabelKey.GCR_SEND_REPORT, ButtonLabelKey.COMMON_CANCEL);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);

    }

    public void persistReport(StateContext<CreateReportState, CreateReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        LocalDate date = DateTimeUtils.parseDefaultDate(CommonUtils.getContextVarAsString(context, ContextVarKey.DATE));
        String timeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TIME_RECORDS_JSON);

        Report report = reportService.getReportByDateAndChatId(date, chatId);

        if (Objects.isNull(report)) {
            report = new Report();

            User user = runtimeDialogManager.getPrincipalUser(chatId);

            report.setDate(date);
            report.setUser(user);
        }

        List<TimeRecord> timeRecordEntities = timeRecordService.convertToTimeRecordEntities(timeRecordJson, report);
        report.setTimeRecords(timeRecordEntities);
        reportService.save(report);

        cacheService.evictCache(CacheService.EMPLOYEE_STATISTIC_CACHE, chatId, date);

        log.info("{} report saved - {}", CommonUtils.getContextVarAsString(context, ContextVarKey.LOG_PREFIX), report);

        sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.GCR_REPORT_SUCCESSFUL_CREATED));
    }

    public void declinePersistReport(StateContext<CreateReportState, CreateReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.GCR_DECLINE_CREATING_REPORT));
    }

}
