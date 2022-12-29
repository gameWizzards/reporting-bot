package com.telegram.reporting.dialogs.general_dialogs.edit_report;

import com.telegram.reporting.dialogs.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.dialogs.MessageKey;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.service.CategoryService;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditReportActions {
    private final SendBotMessageService sendBotMessageService;
    private final CategoryService categoryService;
    private final TimeRecordService timeRecordService;
    private final I18nMessageService i18NMessageService;
    private final I18nButtonService i18nButtonService;

    public void requestChooseEditData(StateContext<EditReportState, EditReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String editTimeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_TIME_RECORD_JSON);
        String timeRecordsJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TIME_RECORDS_JSON);

        TimeRecordTO trTO = JsonUtils.deserializeItem(editTimeRecordJson, TimeRecordTO.class);
        String timeRecordMessage = i18NMessageService.convertToTimeRecordMessage(chatId, trTO);

        String message = i18NMessageService.getMessage(chatId, MessageKey.GER_CHOOSE_EDITING_DATA, timeRecordMessage);

        SendMessage sendMessage = new SendMessage(chatId.toString(), message);

        if (i18nButtonService.hasAvailableCategoryButtons(timeRecordsJson)) {
            sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createInlineMarkup(chatId, MenuButtons.MAIN_MENU, 2,
                    ButtonLabelKey.GER_CATEGORY, ButtonLabelKey.GER_SPEND_TIME, ButtonLabelKey.GER_NOTE));
        } else {
            String tipMessage = i18NMessageService.getMessage(chatId, MessageKey.GER_WARNING_EDITING_ALL_OCCUPIED_CATEGORIES);

            sendBotMessageService.sendMessage(chatId, tipMessage);
            sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createInlineMarkup(chatId, MenuButtons.MAIN_MENU, 2,
                    ButtonLabelKey.GER_SPEND_TIME, ButtonLabelKey.GER_NOTE));
        }
    }

    public void sendDataToEdit(StateContext<EditReportState, EditReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String buttonValue = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);
        String editTimeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_TIME_RECORD_JSON);
        TimeRecordTO trTO = JsonUtils.deserializeItem(editTimeRecordJson, TimeRecordTO.class);

        ButtonLabelKey button = ButtonLabelKey.getByKey(buttonValue);

        String message = switch (button) {
            case GER_SPEND_TIME -> i18NMessageService.getMessage(
                    chatId,
                    MessageKey.GER_REQUEST_CHANGE_HOURS,
                    trTO.getHours().toString());
            case GER_CATEGORY -> {
                String categoryLocalizedLabel = i18NMessageService.getMessage(chatId, trTO.getCategoryNameKey());

                yield i18NMessageService.getMessage(
                        chatId,
                        MessageKey.GER_REQUEST_CHANGE_CATEGORY,
                        categoryLocalizedLabel);
            }
            case GER_NOTE -> i18NMessageService.getMessage(
                    chatId,
                    MessageKey.GER_REQUEST_CHANGE_NOTE,
                    trTO.getNote());

            default ->
                    throw new NoSuchElementException("[Edit Report]Can't create message for unmapped button. Button: " + buttonValue);
        };

        String tip = i18NMessageService.getMessage(chatId, MessageKey.GER_CANCEL_EDITING_TIP);

        sendBotMessageService.sendMessage(chatId, message);
        sendBotMessageService.sendMessage(chatId, tip);
    }

    public void editTimeRecord(StateContext<EditReportState, EditReportEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        Optional<String> time = Optional.ofNullable(CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_TIME));
        Optional<String> note = Optional.ofNullable(CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_NOTE));
        Optional<String> categoryName = Optional.ofNullable(CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_CATEGORY_TYPE_KEY));

        String editTimeRecord = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_TIME_RECORD_JSON);

        TimeRecordTO timeRecordTO = JsonUtils.deserializeItem(editTimeRecord, TimeRecordTO.class);

        time.ifPresent(hours -> timeRecordTO.setHours(Integer.parseInt(hours)));
        note.ifPresent(timeRecordTO::setNote);
        categoryName.ifPresent(timeRecordTO::setCategoryNameKey);

        // required to clear variables for next edit data
        variables.remove(ContextVarKey.REPORT_TIME);
        variables.remove(ContextVarKey.REPORT_NOTE);
        variables.remove(ContextVarKey.REPORT_CATEGORY_TYPE_KEY);

        variables.put(ContextVarKey.TARGET_TIME_RECORD_JSON, JsonUtils.serializeItem(timeRecordTO));
    }

    public void requestEditAdditionalData(StateContext<EditReportState, EditReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String editTimeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_TIME_RECORD_JSON);
        TimeRecordTO timeRecordTO = JsonUtils.deserializeItem(editTimeRecordJson, TimeRecordTO.class);
        String timeRecordToMessage = i18NMessageService.convertToTimeRecordMessage(chatId, timeRecordTO);

        String message = i18NMessageService.getMessage(chatId, MessageKey.GER_REQUEST_CHANGE_REPORT, timeRecordToMessage);

        SendMessage sendMessage = new SendMessage(chatId.toString(), message);
        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU,
                ButtonLabelKey.GER_CONFIRM_EDIT_ADDITIONAL_DATA, ButtonLabelKey.GER_DECLINE_EDIT_ADDITIONAL_DATA);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    public void sendCategoryButtons(StateContext<EditReportState, EditReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String timeRecordsJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TIME_RECORDS_JSON);

        List<List<InlineKeyboardButton>> rows = i18nButtonService.getAvailableCategoryInlineButtons(chatId, timeRecordsJson, 2);

        SendMessage sendMessage = new SendMessage(chatId.toString(), i18NMessageService.getMessage(chatId, MessageKey.COMMON_CHOOSE_CATEGORY));

        sendBotMessageService.sendMessageWithKeys(sendMessage, i18nButtonService.createInlineMarkup(chatId, MenuButtons.MAIN_MENU, rows));
    }

    public void requestSaveTimeRecordChanges(StateContext<EditReportState, EditReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String editTimeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_TIME_RECORD_JSON);
        TimeRecordTO timeRecordTO = JsonUtils.deserializeItem(editTimeRecordJson, TimeRecordTO.class);
        String timeRecordMessage = i18NMessageService.convertToTimeRecordMessage(chatId, timeRecordTO);

        String message = i18NMessageService.getMessage(chatId, MessageKey.GER_REQUEST_SEND_CHANGES, timeRecordMessage);

        SendMessage sendMessage = new SendMessage(CommonUtils.currentChatIdString(context), message);
        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU,
                ButtonLabelKey.GER_APPLY_DATA_CHANGES, ButtonLabelKey.COMMON_CANCEL);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    public void saveTimeRecordChanges(StateContext<EditReportState, EditReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String editTimeRecord = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_TIME_RECORD_JSON);
        TimeRecordTO trTO = JsonUtils.deserializeItem(editTimeRecord, TimeRecordTO.class);
        TimeRecord timeRecord = timeRecordService.getById(trTO.getId());
        Category category = categoryService.getCategoryByName(trTO.getCategoryNameKey());

        timeRecord.setHours(trTO.getHours());
        timeRecord.setNote(trTO.getNote());
        timeRecord.setCategory(category);

        timeRecordService.save(timeRecord);
        context.getExtendedState().getVariables().remove(ContextVarKey.TARGET_TIME_RECORD_JSON);
        sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.GER_REPORT_SUCCESSFUL_UPDATED));
    }

    public void requestEditAdditionalTimeRecord(StateContext<EditReportState, EditReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);
        String message = i18NMessageService.getMessage(chatId, MessageKey.GER_REQUEST_EDIT_ADDITIONAL_REPORT, date);

        SendMessage sendMessage = new SendMessage(CommonUtils.currentChatIdString(context), message);

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU,
                ButtonLabelKey.COMMON_YES, ButtonLabelKey.COMMON_NO);
        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }
}
