package com.telegram.reporting.dialogs.guards;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.i18n.MonthKey;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.LockUpdateReportService;
import com.telegram.reporting.service.RuntimeDialogManager;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GuardValidator {

    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService userService;
    private final RuntimeDialogManager runtimeDialogManager;
    private final LockUpdateReportService lockService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18NMessageService;

    public <S, E> boolean validateDate(StateContext<S, E> context) {
        String regexDay = "^([1-9]|0[1-9]|1\\d|2\\d|3[01])$";
        String regexDayMonth = "^([1-9]|0[1-9]|1\\d|2\\d|3[01]).(0[1-9]|1[0-2])$";
        String regexFullDate = "^([1-9]|0[1-9]|1\\d|2\\d|3[01]).(0[1-9]|1[0-2]).(19|20)\\d{2}$";

        Long chatId = CommonUtils.currentChatId(context);
        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);

        if (userInput.matches(regexDay) || userInput.matches(regexDayMonth) || userInput.matches(regexFullDate)) {
            LocalDate reportDate = DateTimeUtils.parseShortDateToLocalDate(userInput);
            User user = runtimeDialogManager.getPrincipalUser(chatId);

            if (reportDate.isBefore(user.getActivated().toLocalDate())) {
                String activatedDate = DateTimeUtils.toDefaultFormat(user.getActivated().toLocalDate());
                String lockMessage = i18NMessageService.getMessage(
                        chatId,
                        MessageKey.GUARD_WARNING_DATE_BEFORE_AUTH,
                        user.getName(),
                        activatedDate,
                        activatedDate);

                sendBotMessageService.sendMessageWithKeys(
                        new SendMessage(chatId.toString(), lockMessage),
                        i18nButtonService.createMainMenuInlineMarkup(chatId));
                return false;
            }

            boolean existManipulateReportDataLock = lockService.lockExist(user.getId(), reportDate);
            if (existManipulateReportDataLock) {
                String localizedMonth = i18NMessageService.getMessage(chatId, MonthKey.getMonthByOrdinal(reportDate.getMonthValue()));
                String lockMessage = i18NMessageService.getMessage(
                        chatId,
                        MessageKey.GUARD_WARNING_PERIOD_LOCKED_TO_EDITING,
                        user.getName(),
                        localizedMonth);

                sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), lockMessage),
                        i18nButtonService.createMainMenuInlineMarkup(chatId));
                return false;
            }
            return true;
        }

        String dateErrorMessage = i18NMessageService.getMessage(chatId, MessageKey.GUARD_WARNING_WRONG_DATE_FORMAT);

        sendBotMessageService.sendMessageWithKeys(
                new SendMessage(chatId.toString(), dateErrorMessage),
                i18nButtonService.createMainMenuInlineMarkup(chatId));

        return false;
    }

    public <S, E> boolean validateMonthDate(StateContext<S, E> context) {
        String regexMonth = "^([1-9]|1[0-2])$";
        String regexMonthYear = "^([1-9]|1[0-2]).(19|20)\\d{2}$";
        Long chatId = CommonUtils.currentChatId(context);

        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);

        if (userInput.matches(regexMonth) || userInput.matches(regexMonthYear)) {
            return true;
        }

        String monthFormatErrorMessage = i18NMessageService.getMessage(chatId, MessageKey.GUARD_WARNING_WRONG_MONTH_FORMAT);

        sendBotMessageService.sendMessageWithKeys(
                new SendMessage(chatId.toString(), monthFormatErrorMessage),
                i18nButtonService.createMainMenuInlineMarkup(chatId));

        return false;
    }

    public <S, E> boolean validateTime(StateContext<S, E> context) {
        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_TIME);
        Long chatId = CommonUtils.currentChatId(context);

        if (userInput.matches("\\d+") && isWithin24Hours(userInput)) {
            return true;
        }

        String timeErrorMessage = i18NMessageService.getMessage(chatId, MessageKey.GUARD_WARNING_WRONG_TIME_FORMAT);

        sendBotMessageService.sendMessageWithKeys(
                new SendMessage(chatId.toString(), timeErrorMessage),
                i18nButtonService.createMainMenuInlineMarkup(chatId));
        return false;
    }

    public <S, E> boolean validateNote(StateContext<S, E> context) {
        int minNoteLength = 5;
        Long chatId = CommonUtils.currentChatId(context);
        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.REPORT_NOTE);
        String lastButtonText = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);
        boolean isSkipNote = ButtonLabelKey.GCR_SKIP_NOTE.value().equals(lastButtonText);
        // TODO add max size limitation of message
        if (isSkipNote || (Objects.nonNull(userInput) && userInput.trim().length() >= minNoteLength)) {
            return true;
        }

        String noteErrorMessage = i18NMessageService.getMessage(
                chatId,
                MessageKey.GUARD_WARNING_SHORT_NOTE_SIZE,
                String.valueOf(minNoteLength));

        sendBotMessageService.sendMessageWithKeys(
                new SendMessage(chatId.toString(), noteErrorMessage),
                i18nButtonService.createMainMenuInlineMarkup(chatId));
        return false;
    }

    public <S, E> boolean validatePhoneInput(StateContext<S, E> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String userInput = CommonUtils.getContextVarAsString(context, ContextVarKey.PHONE);

        String normalizedPhoneNumber = CommonUtils.normalizePhoneNumber(userInput);

        if (!CommonUtils.isCorrectPhoneFormat(normalizedPhoneNumber)) {
            String phoneFormatErrMessage = i18NMessageService.getMessage(chatId, MessageKey.GUARD_WARNING_WRONG_PHONE_FORMAT);

            sendBotMessageService.sendMessageWithKeys(
                    new SendMessage(chatId.toString(), phoneFormatErrMessage),
                    i18nButtonService.createMainMenuInlineMarkup(chatId));
            return false;
        }

        User existingUser = userService.findByPhone(normalizedPhoneNumber);
        if (Objects.nonNull(existingUser)) {
            log.error("Try to create new user with phone number which already use by user. Phone = {}. Existed user = {}", normalizedPhoneNumber, existingUser);

            String userInfo = existingUser.isActivated()
                    ? existingUser.getFullName()
                    : i18NMessageService.getMessage(chatId, MessageKey.GUARD_WARNING_FAILED_DISPLAY_USERNAME);

            String errMessage = i18NMessageService.getMessage(
                    chatId,
                    MessageKey.GUARD_WARNING_DUPLICATE_ADDING_PHONE,
                    normalizedPhoneNumber,
                    userInfo);

            sendBotMessageService.sendMessageWithKeys(
                    new SendMessage(chatId.toString(), errMessage),
                    i18nButtonService.createMainMenuInlineMarkup(chatId));
            return false;
        }

        context.getExtendedState().getVariables().put(ContextVarKey.PHONE, normalizedPhoneNumber);
        return true;
    }

    private boolean isWithin24Hours(String userInput) {
        var hours = Integer.parseInt(userInput);
        return hours > 0 && hours <= 24;
    }
}
