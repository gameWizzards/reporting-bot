package com.telegram.reporting.dialogs.actions.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.ContextVariable;
import com.telegram.reporting.dialogs.MessageEvent;
import com.telegram.reporting.dialogs.actions.ListUsersActions;
import com.telegram.reporting.dialogs.admin.list_users.ListUsersState;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.MessageConvertorUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Slf4j
@Component
public class ListUsersActionsImpl implements ListUsersActions {
    private final TelegramUserService userService;
    private final SendBotMessageService sendBotMessageService;

    public ListUsersActionsImpl(TelegramUserService userService, SendBotMessageService sendBotMessageService) {
        this.userService = userService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void sendListUsers(StateContext<ListUsersState, MessageEvent> context) {
        UserFilter.UserStatus userStatus = (UserFilter.UserStatus) context.getExtendedState()
                .getVariables()
                .getOrDefault(ContextVariable.USER_STATUS, UserFilter.UserStatus.ACTIVE);

        UserFilter filter = UserFilter.builder()
                .userStatus(userStatus)
                .build();
        List<User> users = userService.findUsers(filter);
        String message = MessageConvertorUtils.convertToListUsersMessage(users);
        sendBotMessageService.sendMessage(TelegramUtils.currentChatIdString(context), message);
    }

    public void sendSelectionStatusButtons(StateContext<ListUsersState, MessageEvent> context) {
        UserFilter.UserStatus userStatus = (UserFilter.UserStatus) context.getExtendedState()
                .getVariables()
                .getOrDefault(ContextVariable.USER_STATUS, UserFilter.UserStatus.ACTIVE);

        KeyboardRow buttons = switch (userStatus) {
            case ACTIVE -> KeyboardUtils.createRowButtons(ButtonValue.USER_STATUS_NOT_VERIFIED.text(), ButtonValue.USER_STATUS_DELETED.text());
            case ACTIVE_NOT_VERIFIED -> KeyboardUtils.createRowButtons(ButtonValue.USER_STATUS_ACTIVE.text(), ButtonValue.USER_STATUS_DELETED.text());
            case DELETED -> KeyboardUtils.createRowButtons(ButtonValue.USER_STATUS_ACTIVE.text(), ButtonValue.USER_STATUS_NOT_VERIFIED.text());
        };
        List<ButtonValue> menuButtons = List.of(ButtonValue.RETURN_ADMIN_MENU, ButtonValue.RETURN_MAIN_MENU);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(TelegramUtils.currentChatIdString(context), "Показать прользователей других категорий?"), KeyboardUtils.createKeyboardMarkup(menuButtons, buttons));
    }
}
