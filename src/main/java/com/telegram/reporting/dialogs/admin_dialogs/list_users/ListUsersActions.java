package com.telegram.reporting.dialogs.admin_dialogs.list_users;

import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.UserService;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListUsersActions {
    private final UserService userService;
    private final SendBotMessageService sendBotMessageService;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18NMessageService;

    public void sendListUsers(StateContext<ListUsersState, ListUsersEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        UserFilter.UserStatus userStatus = (UserFilter.UserStatus) context.getExtendedState()
                .getVariables()
                .getOrDefault(ContextVarKey.USER_STATUS, UserFilter.UserStatus.ACTIVE);

        UserFilter filter = UserFilter.builder()
                .userStatus(userStatus)
                .build();
        List<User> users = userService.findUsers(filter);

        String message = i18NMessageService.convertToListUsersMessage(chatId, users);

        sendBotMessageService.sendMessage(chatId, message);
    }

    public void sendSelectionStatusButtons(StateContext<ListUsersState, ListUsersEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        UserFilter.UserStatus userStatus = (UserFilter.UserStatus) context.getExtendedState()
                .getVariables()
                .getOrDefault(ContextVarKey.USER_STATUS, UserFilter.UserStatus.ACTIVE);

        ReplyKeyboard inlineMarkup = switch (userStatus) {
            case ACTIVE -> i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.ADMIN_MENU,
                    ButtonLabelKey.ALU_USER_STATUS_NOT_VERIFIED, ButtonLabelKey.ALU_USER_STATUS_DELETED);

            case ACTIVE_NOT_VERIFIED -> i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.ADMIN_MENU,
                    ButtonLabelKey.ALU_USER_STATUS_ACTIVE, ButtonLabelKey.ALU_USER_STATUS_DELETED);

            case DELETED -> i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.ADMIN_MENU,
                    ButtonLabelKey.ALU_USER_STATUS_ACTIVE, ButtonLabelKey.ALU_USER_STATUS_NOT_VERIFIED);
            default -> throw new IllegalArgumentException("Unsupported user status in ListUsers dialog. Requested status=" + userStatus);
        };

        String message = i18NMessageService.getMessage(chatId, MessageKey.ALU_SHOW_ANOTHER_CATEGORY_USERS);

        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
    }
}
