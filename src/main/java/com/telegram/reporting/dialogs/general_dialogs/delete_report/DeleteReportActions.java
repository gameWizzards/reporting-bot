package com.telegram.reporting.dialogs.general_dialogs.delete_report;

import com.telegram.reporting.dialogs.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.dialogs.MessageKey;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteReportActions {
    private final SendBotMessageService sendBotMessageService;
    private final TimeRecordService timeRecordService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18NMessageService;

    public void requestDeleteConfirmation(StateContext<DeleteReportState, DeleteReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);
        String timeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_TIME_RECORD_JSON);

        TimeRecordTO trTO = JsonUtils.deserializeItem(timeRecordJson, TimeRecordTO.class);
        String timeRecordMessage = i18NMessageService.convertToTimeRecordMessage(chatId, trTO);
        String message = i18NMessageService.getMessage(chatId, MessageKey.GDR_REQUEST_DELETE_REPORT, date, timeRecordMessage);

        SendMessage sendMessage = new SendMessage(chatId.toString(), message);

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU,
                ButtonLabelKey.GDR_CONFIRM_DELETE_TIME_RECORD, ButtonLabelKey.COMMON_CANCEL);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    public void removeTimeRecord(StateContext<DeleteReportState, DeleteReportEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String timeRecordJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_TIME_RECORD_JSON);

        if (StringUtils.isBlank(timeRecordJson)) {
            sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.GDR_FAILURE_DELETING_REPORT));

            throw new NoSuchElementException("Can't find timeRecord to delete on Date = %s"
                    .formatted(CommonUtils.getContextVarAsString(context, ContextVarKey.DATE)));
        }
        TimeRecordTO timeRecordTO = JsonUtils.deserializeItem(timeRecordJson, TimeRecordTO.class);
        timeRecordService.deleteByTimeRecordTO(timeRecordTO);

        log.info("{} timeRecord removed - {}", CommonUtils.getContextVarAsString(context, ContextVarKey.LOG_PREFIX), timeRecordTO);

        sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.GDR_REPORT_SUCCESSFUL_DELETED));
    }
}
