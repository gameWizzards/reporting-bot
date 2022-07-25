package com.telegram.reporting.dialogs.actions.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.AddEmployeeActions;
import com.telegram.reporting.dialogs.manager.add_employee.AddEmployeeState;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Slf4j
@Component
public class AddEmployeeActionsImpl implements AddEmployeeActions {
    private final TelegramUserService userService;
    private final SendBotMessageService sendBotMessageService;

    public AddEmployeeActionsImpl(TelegramUserService userService, SendBotMessageService sendBotMessageService) {
        this.userService = userService;
        this.sendBotMessageService = sendBotMessageService;
    }
    @Override
    public void requestInputEmployeePhone(StateContext<AddEmployeeState, MessageEvent> context) {
        String message = """
                Для добавления нового сотрудника введи его номер телефона.
                Но лучше скопируй и вставь. Это позволит избежать ошибок))
                Допустимо сокращенное (097...) и полное (+380...) написание""";
        ReplyKeyboardMarkup keyboardMarkup = KeyboardUtils.createKeyboardMarkup(false, KeyboardUtils.createRowButtons(ButtonValue.RETURN_MANAGER_MENU.text(), ButtonValue.RETURN_MAIN_MENU.text()));
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message), keyboardMarkup);
    }

    @Override
    public void saveNewEmployeePhone(StateContext<AddEmployeeState, MessageEvent> context) {
        String phone = TelegramUtils.getContextVariableValueAsString(context, ContextVariable.PHONE);
        User user = new User();
        user.setPhone(phone);
        userService.save(user);
        String message = "Пользователь с номером телефона +%s успешно добавлен".formatted(phone);
        ReplyKeyboardMarkup keyboardMarkup = KeyboardUtils.createKeyboardMarkup(false, KeyboardUtils.createRowButtons(ButtonValue.RETURN_MANAGER_MENU.text(), ButtonValue.RETURN_MAIN_MENU.text()));
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), message), keyboardMarkup);
    }
}
