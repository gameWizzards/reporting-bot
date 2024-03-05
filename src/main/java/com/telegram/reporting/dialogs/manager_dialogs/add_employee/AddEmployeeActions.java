package com.telegram.reporting.dialogs.manager_dialogs.add_employee;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.service.impl.MenuButtons;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.UserService;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddEmployeeActions {
    private final UserService userService;
    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18nMessageService;
    private final I18nButtonService i18nButtonService;

    public void requestInputEmployeePhone(StateContext<AddEmployeeState, AddEmployeeEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String message = i18nMessageService.getMessage(chatId, MessageKey.MAE_PHONE_INPUT_TIP);

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU,
                ButtonLabelKey.COMMON_RETURN_MANAGER_MENU);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
    }

    public void saveNewEmployeePhone(StateContext<AddEmployeeState, AddEmployeeEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String phone = CommonUtils.getContextVarAsString(context, ContextVarKey.PHONE);
        User user = new User();
        user.setPhone(phone);
        userService.save(user);
        String message = i18nMessageService.getMessage(chatId, MessageKey.MAE_USER_SUCCESSFUL_ADDED, phone);

        ReplyKeyboard inlineMarkup = i18nButtonService.createSingleRowInlineMarkup(chatId, MenuButtons.MAIN_MENU,
                ButtonLabelKey.COMMON_RETURN_MANAGER_MENU);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
    }
}