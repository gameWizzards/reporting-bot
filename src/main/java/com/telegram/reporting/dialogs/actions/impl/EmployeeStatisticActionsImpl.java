package com.telegram.reporting.dialogs.actions.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.EmployeeStatisticActions;
import com.telegram.reporting.dialogs.manager.employee_statistic.EmployeeStatisticState;
import com.telegram.reporting.exception.MismatchButtonValueException;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.repository.dto.EmployeeTO;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.service.LockUpdateReportService;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EmployeeStatisticActionsImpl implements EmployeeStatisticActions {
    private final ReportService reportService;
    private final TelegramUserService userService;
    private final SendBotMessageService sendBotMessageService;
    private final LockUpdateReportService lockService;

    public EmployeeStatisticActionsImpl(ReportService reportService, TelegramUserService userService,
                                        SendBotMessageService sendBotMessageService, LockUpdateReportService lockService) {
        this.reportService = reportService;
        this.userService = userService;
        this.sendBotMessageService = sendBotMessageService;
        this.lockService = lockService;
    }

    @Override
    public void requestInputMonth(StateContext<EmployeeStatisticState, MessageEvent> context) {
        String message = "Для вывода статистики введи месяц (Допустимо - месяц, месяц.год)";
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), message);
    }

    @Override
    public void sendListUsers(StateContext<EmployeeStatisticState, MessageEvent> context) {
        String chatId = String.valueOf(TelegramUtils.currentChatId(context));
        String date = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.DATE);
        LocalDate statisticDate = DateTimeUtils.parseDefaultDate(date);
        List<ButtonValue> menuButtons = List.of(ButtonValue.RETURN_MANAGER_MENU, ButtonValue.RETURN_MAIN_MENU);
        List<EmployeeTO> employeeTOs = userService.findEmployeesWithExistReportsByMonth(statisticDate);

        if (employeeTOs.isEmpty()) {
            String message = "За %s ни у одного сотрудника нет отчетов. Попробуй ввести новую дату".formatted(Month.getNameByOrdinal(statisticDate.getMonthValue()));
            sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId, message),
                    KeyboardUtils.createKeyboardMarkup(menuButtons, KeyboardUtils.createButton(ButtonValue.INPUT_NEW_DATE.text())));
            return;
        }

        String listEmployeesMessage = MessageConvertorUtils.convertToListEmployeeMessage(employeeTOs);
        String[] buttons = KeyboardUtils.getButtonsByOrdinalNumber(employeeTOs);
        String employeesTOJson = JsonUtils.serializeItem(employeeTOs);
        context.getExtendedState().getVariables().put(ContextVariable.LIST_EMPLOYEES_JSON, employeesTOJson);

        String message = """
                Список сотрудников, у кого есть отчеты за - %s.
                                
                 %s
                Выберите сотрудника из списка.
                """.formatted(Month.getNameByOrdinal(statisticDate.getMonthValue()), listEmployeesMessage);

        SendMessage sendMessage = new SendMessage(chatId, message);
        KeyboardRow[] buttonsWithRows = KeyboardUtils.createButtonsWithRows(buttons, 10);

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(menuButtons, buttonsWithRows));

    }

    @Override
    public void sendListTimeRecords(StateContext<EmployeeStatisticState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        Long userOrdinalNumber = Long.parseLong((String) variables.get(ContextVariable.EMPLOYEE_ORDINAL));
        String date = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.DATE);
        LocalDate statisticDate = DateTimeUtils.parseDefaultDate(date);

        String employeesTOJson = (String) variables.get(ContextVariable.LIST_EMPLOYEES_JSON);
        List<EmployeeTO> employeeTOS = JsonUtils.deserializeListItems(employeesTOJson, EmployeeTO.class);

        EmployeeTO employeeTO = employeeTOS.stream()
                .filter(empl -> empl.getOrdinalNumber().equals(userOrdinalNumber))
                .findFirst()
                .orElseThrow(() -> new TelegramUserException("Can't get employeeTO by his ordinalNumber"));

        List<Report> reports = reportService.getReportsBelongMonth(statisticDate, employeeTO.getChatId());

        String targetEmployee = JsonUtils.serializeItem(employeeTO);
        variables.put(ContextVariable.TARGET_EMPLOYEE_JSON, targetEmployee);

        AtomicInteger ordinalNumb = new AtomicInteger(1);
        Map<String, Integer> categoryHours = new HashMap<>();

        reports.parallelStream()
                .map(Report::getTimeRecords)
                .flatMap(Collection::stream)
                .forEach(tr -> categoryHours.put(tr.getCategory().getName(), categoryHours.getOrDefault(tr.getCategory().getName(), 0) + tr.getHours()));

        int sumHours = categoryHours.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        String categoryHoursMessage = MessageConvertorUtils.prepareHoursByCategoryMessage(categoryHours);

        String statistic = reports.stream()
                .map(MessageConvertorUtils::convertToStatisticMessage)
                .map(s -> ordinalNumb.getAndIncrement() + ". " + s)
                .collect(Collectors.joining());

        String statisticMessage = """
                %s
                Общее время за %s - %d ч.
                %s
                %s
                """.formatted(employeeTO.getFullName(),
                Month.getNameByOrdinal(statisticDate.getMonthValue()),
                sumHours,
                categoryHoursMessage,
                statistic);

        sendBotMessageService.sendMessage(TelegramUtils.currentChatIdString(context), statisticMessage);
    }

    @Override
    public void requestToLockDataToEdit(StateContext<EmployeeStatisticState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        String date = (String) variables.get(ContextVariable.DATE);
        LocalDate statisticDate = DateTimeUtils.parseDefaultDate(date);
        String employeeTOJson = (String) variables.get(ContextVariable.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        boolean existManipulateReportDataLock = lockService.lockExist(employeeTO.getId(), statisticDate);

        String month = Month.getNameByOrdinal(statisticDate.getMonthValue());
        String startMessage = existManipulateReportDataLock ?
                "%s заблокирован для изменений. %s не может создавать/удалять/изменять отчеты за этот период.".formatted(month, employeeTO.getFullName())
                : "%s все еще может создавать/удалять/изменять отчеты за %s.".formatted(employeeTO.getFullName(), month);
        String message = """
                %s
                           
                Хочешь изменить статус блокировки?
                """.formatted(startMessage);

        variables.put(ContextVariable.LOCK_EDIT_REPORT_MESSAGE, startMessage);
        variables.put(ContextVariable.EXIST_LOCK_EDIT_REPORT, existManipulateReportDataLock);

        KeyboardRow rowButtons = KeyboardUtils.createRowButtons(ButtonValue.YES.text(), ButtonValue.NO.text());
        List<ButtonValue> menuButtons = List.of(ButtonValue.RETURN_MANAGER_MENU, ButtonValue.RETURN_MAIN_MENU);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message),
                KeyboardUtils.createKeyboardMarkup(menuButtons, rowButtons));
    }

    @Override
    public void sendLockReportStatusInfo(StateContext<EmployeeStatisticState, MessageEvent> context) {
        String userLockStatus = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.LOCK_EDIT_REPORT_MESSAGE);
        boolean existLockEditReport = (boolean) context.getExtendedState().getVariables().get(ContextVariable.EXIST_LOCK_EDIT_REPORT);
        String message = """
                Блокировка изменений нужна для сохранения статистических данных за прошедшие периоды.
                Включай блокировку сразу после сверки с сотрудником!
                Это позволит поддерживать актуальные данные и не даст возможность изменить/испортить статистику в будущем!
                        
                %s
                                
                Измени текущий статус блокировки:
                """.formatted(userLockStatus);

        ButtonValue changeLockEditReportStatus = existLockEditReport ?
                ButtonValue.UNLOCK_EDIT_REPORT_DATA : ButtonValue.LOCK_EDIT_REPORT_DATA;

        KeyboardRow rowButtons = KeyboardUtils.createRowButtons(changeLockEditReportStatus.text(), ButtonValue.CANCEL.text());
        List<ButtonValue> menuButtons = List.of(ButtonValue.RETURN_MANAGER_MENU, ButtonValue.RETURN_MAIN_MENU);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message),
                KeyboardUtils.createKeyboardMarkup(menuButtons, rowButtons));
    }

    @Override
    public void handleChoiceLockReportDataToEdit(StateContext<EmployeeStatisticState, MessageEvent> context) {
        String employeeTOJson = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.TARGET_EMPLOYEE_JSON);
        String buttonValue = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.BUTTON_VALUE);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        ButtonValue userChoice = ButtonValue.getByText(buttonValue)
                .orElseThrow(() -> new MismatchButtonValueException("Can't find button with name=%s".formatted(buttonValue)));
        String date = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.DATE);
        LocalDate statisticDate = DateTimeUtils.parseDefaultDate(date);

        String message = "EMPTY";
        switch (userChoice) {
            case LOCK_EDIT_REPORT_DATA -> {
                lockService.saveLock(employeeTO.getId(), statisticDate);
                message = """
                        Отчеты за %s успешно заблокированы!
                        %s не сможет создавать/удалять/изменять отчеты за этот период.
                        """.formatted(Month.getNameByOrdinal(statisticDate.getMonthValue()), employeeTO.getFullName());
            }
            case UNLOCK_EDIT_REPORT_DATA -> {
                lockService.deleteLock(employeeTO.getId(), statisticDate);
                message = """
                        Отчеты за %s доступны для изменеий.
                        %s сможет создавать/удалять/изменять отчеты за этот период.
                        Не забудь заблокировать отчеты когда появится возможность.
                        """.formatted(Month.getNameByOrdinal(statisticDate.getMonthValue()), employeeTO.getFullName());
            }
        }
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), message);
    }

    @Override
    public void requestReturnToListEmployees(StateContext<EmployeeStatisticState, MessageEvent> context) {
        String message = "Хочешь просмотреть отчеты другого сотрудника?";
        List<ButtonValue> menuButtons = List.of(ButtonValue.RETURN_MANAGER_MENU, ButtonValue.RETURN_MAIN_MENU);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message),
                KeyboardUtils.createKeyboardMarkup(menuButtons, KeyboardUtils.createButton(ButtonValue.CHOICE_ANOTHER_EMPLOYEE.text())));

    }
}
