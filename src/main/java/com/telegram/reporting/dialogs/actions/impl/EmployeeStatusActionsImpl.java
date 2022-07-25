package com.telegram.reporting.dialogs.actions.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.EmployeeStatusActions;
import com.telegram.reporting.dialogs.manager.employee_status.EmployeeStatusState;
import com.telegram.reporting.exception.MismatchButtonValueException;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.repository.dto.EmployeeTO;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.JsonUtils;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.MessageConvertorUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EmployeeStatusActionsImpl implements EmployeeStatusActions {
    private final TelegramUserService userService;
    private final SendBotMessageService sendBotMessageService;


    public EmployeeStatusActionsImpl(TelegramUserService userService, SendBotMessageService sendBotMessageService) {
        this.userService = userService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void requestListEmployeesChoose(StateContext<EmployeeStatusState, MessageEvent> context) {
        String message = """
                Выбери необходимый список сотрудников.
                <u>Активные</u> - в список входят все, кто имеет доступ к приложению (в том числе те, кто еще не авторизировался).
                <u>Удаленные</u> - в список входят все, у кого запрещен доступ к приложению.
                """;

        KeyboardRow rowButtons = KeyboardUtils.createRowButtons(ButtonValue.USER_STATUS_ACTIVE.text(), ButtonValue.USER_STATUS_DELETED.text());
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message),
                KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS, rowButtons));
    }

    @Override
    public void sendListUsers(StateContext<EmployeeStatusState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        String chatId = String.valueOf(TelegramUtils.currentChatId(context));

        String textButtonValue = (String) variables.get(ContextVariable.BUTTON_VALUE);

        ButtonValue buttonValue = ButtonValue.getByText(textButtonValue)
                .orElseThrow(() -> new MismatchButtonValueException("Can't find button with name=%s".formatted(textButtonValue)));

        UserFilter filter = switch (buttonValue) {
            case USER_STATUS_ACTIVE -> UserFilter.builder()
                    .userStatus(UserFilter.UserStatus.ACTIVE, UserFilter.UserStatus.ACTIVE_NOT_VERIFIED)
                    .build();
            case USER_STATUS_DELETED -> UserFilter.builder()
                    .userStatus(UserFilter.UserStatus.DELETED)
                    .build();
            default -> null;
        };

        List<EmployeeTO> employeeTOs = userService.findUsers(filter).stream()
                .map(EmployeeTO::new)
                .toList();

        if (employeeTOs.isEmpty()) {
            String message = """
                    Нет пользователей в статусе: <u>%s</>!
                    Хочешь выбрать другой список?
                    """.formatted(textButtonValue);
            sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId, message),
                    KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS,
                            KeyboardUtils.createButton(ButtonValue.CHOICE_ANOTHER_LIST_EMPLOYEES.text())));
            return;
        }

        String listEmployeesMessage = MessageConvertorUtils.convertToListEmployeeMessage(employeeTOs);
        String[] buttons = KeyboardUtils.getButtonsByOrdinalNumber(employeeTOs);
        String employeesTOJson = JsonUtils.serializeItem(employeeTOs);
        context.getExtendedState().getVariables().put(ContextVariable.LIST_EMPLOYEES_JSON, employeesTOJson);

        String message = """
                Список сотрудников, в статусе - <u>%s</u>.
                                
                 %s
                Выберите сотрудника из списка.
                """.formatted(textButtonValue, listEmployeesMessage);

        SendMessage sendMessage = new SendMessage(chatId, message);
        KeyboardRow[] buttonsWithRows = KeyboardUtils.createButtonsWithRows(buttons, 10);

        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS, buttonsWithRows));
    }

    @Override
    public void sendEmployeeInfo(StateContext<EmployeeStatusState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        String message = """
                %s
                Выбери данные из доступных, которые ты хочешь изменить.
                """;

        String targetEmployeeJson = (String) variables.get(ContextVariable.TARGET_EMPLOYEE_JSON);
        boolean isAdditionalUpdateEmployee = targetEmployeeJson != null;

        if (isAdditionalUpdateEmployee) {
            EmployeeTO employeeTO = JsonUtils.deserializeItem(targetEmployeeJson, EmployeeTO.class);
            String employeeInfo = MessageConvertorUtils.convert2EmployeeStatusInfoMessage(employeeTO);

            sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message.formatted(employeeInfo)),
                    KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS,
                            KeyboardUtils.createRowButtons(ButtonValue.CHANGE_EMPLOYEE_STATUS.text(), ButtonValue.CHANGE_EMPLOYEE_ROLE.text())));
            return;
        }

        Long employeeOrdinalNumber = Long.parseLong((String) variables.get(ContextVariable.EMPLOYEE_ORDINAL));
        String employeesTOJson = (String) variables.get(ContextVariable.LIST_EMPLOYEES_JSON);
        List<EmployeeTO> employeeTOS = JsonUtils.deserializeListItems(employeesTOJson, EmployeeTO.class);

        EmployeeTO employeeTO = employeeTOS.stream()
                .filter(empl -> empl.getOrdinalNumber().equals(employeeOrdinalNumber))
                .findFirst()
                .orElseThrow(() -> new TelegramUserException("Can't get employeeTO by his ordinalNumber"));

        String targetEmployee = JsonUtils.serializeItem(employeeTO);
        variables.put(ContextVariable.TARGET_EMPLOYEE_JSON, targetEmployee);

        String employeeInfo = MessageConvertorUtils.convert2EmployeeStatusInfoMessage(employeeTO);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message.formatted(employeeInfo)),
                KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS,
                        KeyboardUtils.createRowButtons(ButtonValue.CHANGE_EMPLOYEE_STATUS.text(), ButtonValue.CHANGE_EMPLOYEE_ROLE.text())));

    }

    @Override
    public void sendEditStatusInfo(StateContext<EmployeeStatusState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String employeeTOJson = (String) variables.get(ContextVariable.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String employeeInfo = MessageConvertorUtils.convert2EmployeeStatusInfoMessage(employeeTO);

        ButtonValue changeStatus = employeeTO.isDeleted() ? ButtonValue.ACTIVATE_EMPLOYEE : ButtonValue.DELETE_EMPLOYEE;

        String message = """
                Ты хочешь изменить данные для:
                %s
                                
                Изменить статус сотрудника?
                """.formatted(employeeInfo);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message),
                KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS,
                        KeyboardUtils.createRowButtons(changeStatus.text(), ButtonValue.CANCEL.text())));
    }

    @Override
    public void handleEmployeeEditStatus(StateContext<EmployeeStatusState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String employeeTOJson = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String textButtonValue = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.BUTTON_VALUE);

        ButtonValue buttonValue = ButtonValue.getByText(textButtonValue)
                .orElseThrow(() -> new MismatchButtonValueException("Can't find button with name=%s".formatted(textButtonValue)));
        User user = userService.findById(employeeTO.getId())
                .orElseThrow(() -> new TelegramUserException("Can't find user with id = %s".formatted(employeeTO.getId())));

        switch (buttonValue) {
            case ACTIVATE_EMPLOYEE -> user.setDeleted(false);
            case DELETE_EMPLOYEE -> user.setDeleted(true);
        }
        User savedUser = userService.save(user);
        updateTargetEmployeeJson(savedUser, variables);
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Статус сотрудника успешно обновлен!");
    }

    @Override
    public void sendEditRoleInfo(StateContext<EmployeeStatusState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String employeeTOJson = (String) variables.get(ContextVariable.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String employeeInfo = MessageConvertorUtils.convert2EmployeeStatusInfoMessage(employeeTO);
        String message = """
                Ты хочешь изменить данные для:
                %s
                                
                %s
                """;

        if (!employeeTO.isActivated()) {
            String dataEditMessage = "Сотрудник не авторизировался! Невозможно добавть новую роль!";
            sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message.formatted(employeeInfo, dataEditMessage)),
                    KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS, KeyboardUtils.createRowButtons(ButtonValue.CANCEL.text())));
            return;
        }

        String dataEditMessage = "Изменить роли сотрудника?";
        boolean hasManagerRole = employeeTO.getRoles().contains(Role.MANAGER_ROLE);
        ButtonValue changeManagerRole = hasManagerRole ? ButtonValue.REMOVE_MANAGER_ROLE : ButtonValue.ADD_MANAGER_ROLE;

        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message.formatted(employeeInfo, dataEditMessage)),
                KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS,
                        KeyboardUtils.createRowButtons(changeManagerRole.text(), ButtonValue.CANCEL.text())));
    }

    @Override
    public void handleEmployeeEditRole(StateContext<EmployeeStatusState, MessageEvent> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        String employeeTOJson = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String textButtonValue = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.BUTTON_VALUE);

        ButtonValue buttonValue = ButtonValue.getByText(textButtonValue)
                .orElseThrow(() -> new MismatchButtonValueException("Can't find button with name=%s".formatted(textButtonValue)));
        User user = userService.findById(employeeTO.getId())
                .orElseThrow(() -> new TelegramUserException("Can't find user with id = %s".formatted(employeeTO.getId())));

        switch (buttonValue) {
            case ADD_MANAGER_ROLE -> user.getRoles().add(Role.MANAGER_ROLE);
            case REMOVE_MANAGER_ROLE -> user.getRoles().remove(Role.MANAGER_ROLE);
        }
        User savedUser = userService.save(user);

        updateTargetEmployeeJson(savedUser, variables);

        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(context), "Роли сотрудника успешно обновлены!");
    }

    @Override
    public void requestChangeAdditionalData(StateContext<EmployeeStatusState, MessageEvent> context) {
        String employeeTOJson = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String employeeInfo = MessageConvertorUtils.convert2EmployeeStatusInfoMessage(employeeTO);
        String message = """
                Хочешь еще изменть данные сотрудника?
                %s
                """.formatted(employeeInfo);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message),
                KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS,
                        KeyboardUtils.createRowButtons(ButtonValue.YES.text(), ButtonValue.NO.text())));
    }

    @Override
    public void requestReturnToListEmployees(StateContext<EmployeeStatusState, MessageEvent> context) {
        String message = "Хочешь выбрать другого сотрудника?";
        context.getExtendedState().getVariables().remove(ContextVariable.TARGET_EMPLOYEE_JSON);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message),
                KeyboardUtils.createKeyboardMarkup(KeyboardUtils.MANAGER_MENU_BUTTONS, KeyboardUtils.createButton(ButtonValue.CHOICE_ANOTHER_EMPLOYEE.text())));

    }

    private void updateTargetEmployeeJson(User savedUser, Map<Object, Object> variables) {
        EmployeeTO updatedTargetEmployee = new EmployeeTO(savedUser);
        String updatedTargetEmployeeJson = JsonUtils.serializeItem(updatedTargetEmployee);
        variables.put(ContextVariable.TARGET_EMPLOYEE_JSON, updatedTargetEmployeeJson);
    }
}
