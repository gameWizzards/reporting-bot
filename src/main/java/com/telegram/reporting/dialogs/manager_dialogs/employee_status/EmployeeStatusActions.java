package com.telegram.reporting.dialogs.manager_dialogs.employee_status;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.dto.EmployeeTO;
import com.telegram.reporting.domain.Role;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.UserService;
import com.telegram.reporting.utils.CommonUtils;
import com.telegram.reporting.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeStatusActions {
    private final UserService userService;
    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18NMessageService;
    private final I18nButtonService i18nButtonService;

    public void requestListEmployeesChoose(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String message = i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_CHOOSE_EMPLOYEE_LIST);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message),
                i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                        ButtonLabelKey.ALU_USER_STATUS_ACTIVE, ButtonLabelKey.ALU_USER_STATUS_DELETED));
    }

    public void sendListUsers(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);

        String buttonKey = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);

        ButtonLabelKey buttonLabelKey = ButtonLabelKey.getByKey(buttonKey);

        UserFilter filter = switch (buttonLabelKey) {
            case ALU_USER_STATUS_ACTIVE -> UserFilter.builder()
                    .userStatus(UserFilter.UserStatus.ACTIVE, UserFilter.UserStatus.ACTIVE_NOT_VERIFIED)
                    .build();
            case ALU_USER_STATUS_DELETED -> UserFilter.builder()
                    .userStatus(UserFilter.UserStatus.DELETED)
                    .build();
            default ->
                    throw new IllegalArgumentException("Can't find mapping of button to user status. Button=" + buttonLabelKey);
        };

        List<EmployeeTO> employeeTOs = userService.findUsers(filter).stream()
                .map(EmployeeTO::new)
                .toList();

        if (employeeTOs.isEmpty()) {
            String chosenStatus = i18NMessageService.getMessage(chatId, buttonLabelKey);
            String message = i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_CHOOSE_ANOTHER_EMPLOYEE_LIST, chosenStatus);

            sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message),
                    i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                            ButtonLabelKey.MESTATUS_CHOICE_ANOTHER_LIST_EMPLOYEES));
            return;
        }

        String listEmployeesMessage = i18NMessageService.convertToListEmployeeMessage(chatId, employeeTOs);

        String employeesTOJson = JsonUtils.serializeItem(employeeTOs);
        context.getExtendedState().getVariables().put(ContextVarKey.LIST_EMPLOYEES_JSON, employeesTOJson);

        String chosenStatus = i18NMessageService.getMessage(chatId, buttonKey);
        String message = i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_CHOOSE_EMPLOYEE_EDIT_STATUS,
                chosenStatus, listEmployeesMessage);

        SendMessage sendMessage = new SendMessage(chatId.toString(), message);
        ReplyKeyboard inlineMarkup = i18nButtonService.createOrdinalButtonsInlineMarkup(
                chatId, MenuButtons.MANAGER_MENU, employeeTOs, 10);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    public void sendEmployeeInfo(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        String employeeInfo;
        Long chatId = CommonUtils.currentChatId(context);
        String targetEmployeeJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_EMPLOYEE_JSON);

        boolean isAdditionalUpdateEmployee = Objects.nonNull(targetEmployeeJson);

        if (isAdditionalUpdateEmployee) {
            EmployeeTO employeeTO = JsonUtils.deserializeItem(targetEmployeeJson, EmployeeTO.class);
            employeeInfo = i18NMessageService.convert2EmployeeStatusInfoMessage(chatId, employeeTO);
        } else {
            Long employeeOrdinalNumber = CommonUtils.getContextVar(context, Long.class, ContextVarKey.EMPLOYEE_ORDINAL);
            String employeesTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.LIST_EMPLOYEES_JSON);
            List<EmployeeTO> employeeTOS = JsonUtils.deserializeListItems(employeesTOJson, EmployeeTO.class);

            EmployeeTO employeeTO = employeeTOS.stream()
                    .filter(empl -> empl.getOrdinalNumber().equals(employeeOrdinalNumber))
                    .findFirst()
                    .orElseThrow(() -> new TelegramUserException("Can't get employeeTO by his ordinalNumber: " + employeeOrdinalNumber));

            String employeeJson = JsonUtils.serializeItem(employeeTO);
            context.getExtendedState().getVariables().put(ContextVarKey.TARGET_EMPLOYEE_JSON, employeeJson);

            employeeInfo = i18NMessageService.convert2EmployeeStatusInfoMessage(chatId, employeeTO);
        }


        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_CHOOSE_AVAILABLE_EDIT_DATA, employeeInfo));

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                ButtonLabelKey.MESTATUS_CHANGE_EMPLOYEE_STATUS, ButtonLabelKey.MESTATUS_CHANGE_EMPLOYEE_ROLE);
        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    public void sendEditStatusInfo(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);

        String employeeTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String employeeInfo = i18NMessageService.convert2EmployeeStatusInfoMessage(chatId, employeeTO);

        ButtonLabelKey changeStatus = employeeTO.isDeleted()
                ? ButtonLabelKey.MESTATUS_ACTIVATE_EMPLOYEE
                : ButtonLabelKey.MESTATUS_DELETE_EMPLOYEE;

        String changeStatusMessage = i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_REQUEST_CHANGE_EMPLOYEE_STATUS);

        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_REQUEST_CHANGE_EMPLOYEE_DATA,
                        employeeInfo, changeStatusMessage));

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                changeStatus, ButtonLabelKey.COMMON_CANCEL);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    public void handleEmployeeEditStatus(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String employeeTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String textButtonValue = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);

        ButtonLabelKey buttonLabelKey = ButtonLabelKey.getByKey(textButtonValue);
        User user = userService.findById(employeeTO.getId());

        switch (buttonLabelKey) {
            case MESTATUS_ACTIVATE_EMPLOYEE -> user.setDeleted(false);
            case MESTATUS_DELETE_EMPLOYEE -> user.setDeleted(true);
        }

        User savedUser = userService.save(user);
        updateTargetEmployeeJson(savedUser, context.getExtendedState().getVariables());

        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_EMPLOYEE_STATUS_SUCCESSFUL_UPDATED));
    }

    public void sendEditRoleInfo(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String employeeTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String employeeInfo = i18NMessageService.convert2EmployeeStatusInfoMessage(chatId, employeeTO);


        if (!employeeTO.isActivated()) {
            String warning = i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_WARNING_CANNOT_CHANGE_ROLE);

            SendMessage sendMessage = new SendMessage(chatId.toString(),
                    i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_REQUEST_CHANGE_EMPLOYEE_DATA,
                            employeeInfo, warning));

            ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                    ButtonLabelKey.COMMON_CANCEL);

            sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
            return;
        }

        String changeRoleMessage = i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_REQUEST_CHANGE_EMPLOYEE_ROLE);

        boolean hasManagerRole = employeeTO.getRoles().contains(Role.MANAGER_ROLE);
        ButtonLabelKey changeManagerRole = hasManagerRole
                ? ButtonLabelKey.MESTATUS_REMOVE_MANAGER_ROLE
                : ButtonLabelKey.MESTATUS_ADD_MANAGER_ROLE;

        SendMessage sendMessage = new SendMessage(chatId.toString(),
                i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_REQUEST_CHANGE_EMPLOYEE_DATA,
                        employeeInfo, changeRoleMessage));

        ReplyKeyboard inlineMarkup = i18nButtonService.createInlineMarkup(chatId, MenuButtons.MANAGER_MENU, 1,
                changeManagerRole, ButtonLabelKey.COMMON_CANCEL);

        sendBotMessageService.sendMessageWithKeys(sendMessage, inlineMarkup);
    }

    public void handleEmployeeEditRole(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);

        String employeeTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String textButtonValue = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);

        ButtonLabelKey buttonLabelKey = ButtonLabelKey.getByKey(textButtonValue);
        User user = userService.findById(employeeTO.getId());

        switch (buttonLabelKey) {
            case MESTATUS_ADD_MANAGER_ROLE -> user.getRoles().add(Role.MANAGER_ROLE);
            case MESTATUS_REMOVE_MANAGER_ROLE -> user.getRoles().remove(Role.MANAGER_ROLE);
        }
        User savedUser = userService.save(user);

        updateTargetEmployeeJson(savedUser, context.getExtendedState().getVariables());

        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_EMPLOYEE_ROLES_SUCCESSFUL_UPDATED));
    }

    public void requestChangeAdditionalData(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String employeeTOJson = CommonUtils.getContextVarAsString(context, ContextVarKey.TARGET_EMPLOYEE_JSON);
        EmployeeTO employeeTO = JsonUtils.deserializeItem(employeeTOJson, EmployeeTO.class);
        String employeeInfo = i18NMessageService.convert2EmployeeStatusInfoMessage(chatId, employeeTO);
        String message = i18NMessageService.getMessage(chatId, MessageKey.MESTATUS_REQUEST_CHANGE_ADDITIONAL_EMPLOYEE_DATA,
                employeeInfo);

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                ButtonLabelKey.COMMON_YES, ButtonLabelKey.COMMON_NO);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message),
                inlineMarkup);
    }

    public void requestReturnToListEmployees(StateContext<EmployeeStatusState, EmployeeStatusEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String message = i18NMessageService.getMessage(chatId, MessageKey.COMMON_REQUEST_CHOOSE_ANOTHER_EMPLOYEE);

        context.getExtendedState().getVariables().remove(ContextVarKey.TARGET_EMPLOYEE_JSON);

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MANAGER_MENU,
                ButtonLabelKey.MES_CHOICE_ANOTHER_EMPLOYEE);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
    }

    private void updateTargetEmployeeJson(User savedUser, Map<Object, Object> variables) {
        EmployeeTO updatedTargetEmployee = new EmployeeTO(savedUser);
        String updatedTargetEmployeeJson = JsonUtils.serializeItem(updatedTargetEmployee);
        variables.put(ContextVarKey.TARGET_EMPLOYEE_JSON, updatedTargetEmployeeJson);
    }
}
