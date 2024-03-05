package com.telegram.reporting.dialogs.general_dialogs.language;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.dialogs.ContextVarKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.I18nPropsResolver;
import com.telegram.reporting.service.RuntimeDialogManager;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class LanguageActions {
    private final SendBotMessageService sendBotMessageService;
    private final UserService userService;
    private final RuntimeDialogManager runtimeDialogManager;
    private final I18nButtonService i18nButtonService;
    private final I18nMessageService i18nMessageService;
    private final DialogRouterService dialogRouterService;

    public void sendListLanguages(StateContext<LanguageState, LanguageEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String message = i18nMessageService.getMessage(chatId, MessageKey.GL_CHOICE_LANGUAGE);

        List<List<InlineKeyboardButton>> languageButtons = i18nButtonService.getLanguageInlineButtons(chatId);

        ReplyKeyboard inlineMarkup = i18nButtonService.createInlineMarkup(chatId, MenuButtons.MAIN_MENU, languageButtons);
        sendBotMessageService.sendMessageWithKeys(new SendMessage(chatId.toString(), message), inlineMarkup);
    }

    public void handleUserLanguageChoice(StateContext<LanguageState, LanguageEvent> context) {
        Long chatId = CommonUtils.currentChatId(context);
        String textButtonValue = CommonUtils.getContextVarAsString(context, ContextVarKey.BUTTON_CALLBACK_VALUE);

        ButtonLabelKey buttonLabelKey = ButtonLabelKey.getByKey(textButtonValue);

        Locale chosenLocale = switch (buttonLabelKey) {
            case GL_UA_LOCALE -> I18nPropsResolver.UA_LOCALE;
            case GL_RU_LOCALE -> I18nPropsResolver.RU_LOCALE;
            default ->
                    throw new IllegalArgumentException("Can't match button value with existing locales. Button value: " + buttonLabelKey);
        };

        User user = runtimeDialogManager.getPrincipalUser(chatId);
        user.setLocale(chosenLocale);
        userService.save(user);

        String chosenLanguage = i18nMessageService.getMessage(chatId, buttonLabelKey);
        sendBotMessageService.sendMessage(chatId, i18nMessageService.getMessage(chatId, MessageKey.GL_RESULT_CHANGING_LANGUAGE, chosenLanguage));
        dialogRouterService.startFlow(chatId, chosenLocale);
    }
}
