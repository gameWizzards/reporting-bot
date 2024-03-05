package com.telegram.reporting.dialogs.manager_dialogs.employee_statistic;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.dto.EmployeeTO;
import com.telegram.reporting.domain.Report;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.LockUpdateReportService;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.UserService;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.DateTimeUtils;
import com.telegram.reporting.utils.JsonUtils;
import com.telegram.reporting.i18n.MonthKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeStatisticActions {
    private final ReportService reportService;
    private final UserService userService;
    private final SendBotMessageService sendBotMessageService;
    private final LockUpdateReportService lockService;
    private final I18nMessageService i18NMessageService;
    private final I18nButtonService i18nButtonService;

    public void requestInputMonth(StateContext<EmployeeStatisticState, EmployeeStatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.MES_INPUT_MONTH));
    }

    public void sendListUsers(StateContext<EmployeeStatisticState, EmployeeStatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);
        LocalDate statisticDate = DateTimeUtils.parseDefaultDate(date);
        String statisticMonth = i18NMessageService.getMessage(chatId, MonthKey.getMonthByOrdinal(statisticDate.getMonthValue()));
        List<EmployeeTO> employeeTOs = userService.findEmployeesWithExistReportsByMonth(statisticDate);

        if (employeeTOs.isEmpty()) {
            String message = i18NMessageService.getMessage(chatId, MessageKey.MES_WARNING_STATISTIC_NOT_EXISTS,
                    statisticMonth);

            ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU, ButtonLabelKey.COMMON_INPUT_NEW_DATE);
            sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
            return;
        }

        String listEmployeesMessage = i18NMessageService.convertToListEmployeeMessage(chatId, employeeTOs);
        String employeesTOJson = JsonUtils.serializeItem(employeeTOs);
        context.getExtendedState().getVariables().put(ContextVarKey.LIST_EMPLOYEES_JSON, employeesTOJson);

        String message = i18NMessageService.getMessage(chatId, MessageKey.MES_CHOOSE_EMPLOYEE_SHOW_STATISTIC,
                statisticMonth, listEmployeesMessage);

        SendMessage sendMessage = new SendMessage(chatId.toString(), message);
        ReplyKeyboard inlineMarkup = i18nButtonService.createOrdinalButtonsInlineMarkup(
                chatId, MenuButtons.MANAGER_MENU, employeeTOs, 10);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);

    }

    public void sendListTimeRecords(StateContext<EmployeeStatisticState, EmployeeStatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        Long userOrdinalNumber = CommonUtils.getContextVar(context, Long.class, ContextVarKey.EMPLOYEE_ORDINAL);
        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);
        LocalDate statisticDate = DateTimeUtils.parseDefaultDate(date);

        String employeesTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.LIST_EMPLOYEES_JSON);
        List<EmployeeTO> employeeTOS = JsonUtils.deserializeListItems(employeesTOJson, EmployeeTO.class);

        EmployeeTO employeeTO = employeeTOS.stream()
                .filter(empl -> empl.getOrdinalNumber().equals(userOrdinalNumber))
                .findFirst()
                .orElseThrow(() -> new TelegramUserException("Can't get employeeTO by his ordinalNumber:" + userOrdinalNumber));


        String targetEmployee = JsonUtils.serializeItem(employeeTO);
        context.getStateMachine().getExtendedState().getVariables().put(ContextVarKey.TARGET_EMPLOYEE_JSON, targetEmployee);

        List<Report> reports = reportService.getReportsBelongMonth(statisticDate, employeeTO.getChatId());

        String monthStatisticMessage = i18NMessageService.createMonthStatisticMessage(chatId, statisticDate, reports);

        String totalMonthEmployeeStatisticMessage = i18NMessageService.getMessage(chatId, MessageKey.MES_EMPLOYEE_MONTH_STATISTIC_MESSAGE,
                employeeTO.getFullName(), monthStatisticMessage);

        sendBotMessageService.sendMessage(chatId, totalMonthEmployeeStatisticMessage);

        sendLink2UserChat(chatId, employeeTO);
    }

    public void requestToLockDataToEdit(StateContext<EmployeeStatisticState, EmployeeStatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);

        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);
        LocalDate statisticDate = DateTimeUtils.parseDefaultDate(date);
        String employeeTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        boolean existManipulateReportDataLock = lockService.lockExist(employeeTO.getId(), statisticDate);

        String localizedMonth = i18NMessageService.getMessage(chatId, MonthKey.getMonthByOrdinal(statisticDate.getMonthValue()));

        String lockStatusMessage = existManipulateReportDataLock
                ? i18NMessageService.getMessage(chatId, MessageKey.MES_PERIOD_LOCKED_TO_EDITING, localizedMonth, employeeTO.getFullName())
                : i18NMessageService.getMessage(chatId, MessageKey.MES_PERIOD_UNLOCKED_TO_EDITING, employeeTO.getFullName(), localizedMonth);

        String message = i18NMessageService.getMessage(chatId, MessageKey.MES_REQUEST_CHANGING_LOCK_STATUS, lockStatusMessage);

        context.getExtendedState().getVariables().put(ContextVarKey.LOCK_EDIT_REPORT_MESSAGE, lockStatusMessage);
        context.getExtendedState().getVariables().put(ContextVarKey.EXIST_LOCK_EDIT_REPORT, existManipulateReportDataLock);

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                ButtonLabelKey.COMMON_YES, ButtonLabelKey.COMMON_NO);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
    }

    public void sendLockReportStatusInfo(StateContext<EmployeeStatisticState, EmployeeStatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String userLockStatus = CommonUtils.getContextVarAsString(context, ContextVarKey.LOCK_EDIT_REPORT_MESSAGE);
        Boolean existLockEditReport = CommonUtils.getContextVar(context, Boolean.class, ContextVarKey.EXIST_LOCK_EDIT_REPORT);

        String message = i18NMessageService.getMessage(chatId, MessageKey.MES_LOCK_EDITING_TIP, userLockStatus);

        ButtonLabelKey changeLockEditReportStatus = existLockEditReport ?
                ButtonLabelKey.MES_UNLOCK_EDIT_REPORT_DATA : ButtonLabelKey.MES_LOCK_EDIT_REPORT_DATA;

        ReplyKeyboard inlineMarkup = i18nButtonService.createInlineMarkup(chatId, MenuButtons.MANAGER_MENU, 1,
                changeLockEditReportStatus, ButtonLabelKey.COMMON_CANCEL);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
    }

    public void handleChoiceLockReportDataToEdit(StateContext<EmployeeStatisticState, EmployeeStatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String employeeTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_EMPLOYEE_JSON);
        String buttonValue = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        ButtonLabelKey userChoice = ButtonLabelKey.getByKey(buttonValue);
        String date = CommonUtils.getContextVarAsString(context, ContextVarKey.DATE);
        LocalDate statisticDate = DateTimeUtils.parseDefaultDate(date);
        String localizedMonth = i18NMessageService.getMessage(chatId, MonthKey.getMonthByOrdinal(statisticDate.getMonthValue()));

        String message = "EMPTY";
        switch (userChoice) {
            case MES_LOCK_EDIT_REPORT_DATA -> {
                lockService.saveLock(employeeTO.getId(), statisticDate);
                message = i18NMessageService.getMessage(chatId, MessageKey.MES_PERIOD_SUCCESSFUL_LOCKED,
                        localizedMonth, employeeTO.getFullName());
            }
            case MES_UNLOCK_EDIT_REPORT_DATA -> {
                lockService.deleteLock(employeeTO.getId(), statisticDate);
                message = i18NMessageService.getMessage(chatId, MessageKey.MES_PERIOD_SUCCESSFUL_UNLOCKED,
                        localizedMonth, employeeTO.getFullName());

            }
        }
        sendBotMessageService.sendMessage(chatId, message);
    }

    public void requestReturnToListEmployees(StateContext<EmployeeStatisticState, EmployeeStatisticEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String message = i18NMessageService.getMessage(chatId, MessageKey.MES_REQUEST_SHOW_ANOTHER_EMPLOYEE_STATISTIC);
        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                ButtonLabelKey.MES_CHOICE_ANOTHER_EMPLOYEE);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
    }

    public void sendLink2UserChat(Long chatId, EmployeeTO employee) {

        String link = i18NMessageService.getMessage(chatId, MessageKey.COMMON_USER_CHAT_LINK, employee.getPhone());

        sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.COMMON_REQUEST_GO_USER_CHAT, link, employee.getFullName()));
    }
}
