package com.telegram.reporting.service.impl;

import com.telegram.reporting.bot.event.DynamicOrdinalInlineButtonEvent;
import com.telegram.reporting.bot.event.InlineButtonEvent;
import com.telegram.reporting.bot.event.SendContactEvent;
import com.telegram.reporting.bot.event.TelegramEvent;
import com.telegram.reporting.bot.event.UserInputEvent;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.MenuTemplateService;
import com.telegram.reporting.service.RuntimeDialogManager;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogRouterServiceImpl implements DialogRouterService {

    private final Map<Long, DialogHandler> activeDialogHandlers = new ConcurrentHashMap<>();

    private final List<DialogHandler> existingDialogHandlers;
    private final SendBotMessageService sendBotMessageService;
    private final UserService userService;
    private final RuntimeDialogManager runtimeDialogManager;
    private final I18nMessageService i18NMessageService;
    private final I18nButtonService i18nButtonService;
    private final MenuTemplateService menuTemplateService;

    @Override
    public void handleTelegramEvent(TelegramEvent event) {
        Long chatId = event.chatId();

        if (event instanceof SendContactEvent sendContactEvent) {
            handleSendContactEvent(chatId, sendContactEvent);
            return;
        }

        Locale principalUserLocale = runtimeDialogManager.getPrincipalUserLocale(chatId);

        if (event instanceof InlineButtonEvent inlineButtonEvent) {
            ButtonLabelKey buttonLabelKey = inlineButtonEvent.buttonLabelKey();

            // bind handlers when buttonValue contains name of particular dialog
            if (menuTemplateService.belongToRootMenu(buttonLabelKey) && isAllowStartDialog(chatId)) {
                bindDialogHandler(chatId, buttonLabelKey);
            }

            // return to root menu when click 'main menu' button
            if (ButtonLabelKey.COMMON_RETURN_MAIN_MENU.equals(buttonLabelKey)) {
                startFlow(chatId, principalUserLocale);
                return;
            }

            if (activeDialogHandlers.containsKey(chatId)) {
                activeDialogHandlers.get(chatId).handleInlineButtonInput(chatId, buttonLabelKey);
                return;
            }

        } else if (activeDialogHandlers.containsKey(chatId) && event instanceof DynamicOrdinalInlineButtonEvent ordinalButtonEvent) {
            activeDialogHandlers.get(chatId)
                    .handleTelegramUserInput(chatId, ordinalButtonEvent.buttonCallbackData());
            return;
        } else if (activeDialogHandlers.containsKey(chatId) && event instanceof UserInputEvent userInputEvent) {
            activeDialogHandlers.get(chatId).handleTelegramUserInput(chatId, userInputEvent.userInput());
            return;
        }

        // when dialog in chat has remained on some step (different from root menu) with buttons but app was reloaded
        // that means that there is no handler for the dialog - start from root menu
        log.error("Can't find appropriate active handler to chat [{}].", chatId);
        sendBotMessageService.sendMessage(chatId,
                i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_SOMETHING_GOING_WRONG));
        startFlow(chatId, principalUserLocale);
    }

    @Override
    public void startFlow(Long chatId, Locale locale) {
        removeDialogRelatedData(chatId);
        User user = runtimeDialogManager.getPrincipalUser(chatId);

        final String startFlowMessage = i18NMessageService.getMessage(
                chatId,
                MessageKey.PD_START_FLOW_MESSAGE,
                user.getName());

        List<List<InlineKeyboardButton>> rootMenuButtons = i18nButtonService.getRootMenuButtons(chatId, user);
        ReplyKeyboard rootMenuMarkup = i18nButtonService.createInlineMarkup(chatId, MenuButtons.NONE, rootMenuButtons);

        SendMessage sendMessage = new SendMessage(chatId.toString(), startFlowMessage);
        sendBotMessageService.sendMessageWithKeys(sendMessage, rootMenuMarkup);
    }

    private void handleSendContactEvent(Long chatId, SendContactEvent sendContactEvent) {
        sendBotMessageService.removeReplyKeyboard(chatId, i18NMessageService.getMessage(chatId, MessageKey.PD_SUCCESS_CONTACT_SHARING));
        try {
            User user = userService.verifyContact(sendContactEvent);
            startFlow(user.getChatId(), user.getLocale());
        } catch (TelegramUserException e) {
            log.error("Can't verify contact! Reason: {}", e.getMessage());
            sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.PD_FAILED_CONTACT_CHECKING));
        }
    }

    private boolean isAllowStartDialog(Long chatId) {
        return runtimeDialogManager.containsPrincipalUser(chatId)
               && !runtimeDialogManager.getPrincipalUser(chatId).isDeleted();
    }

    private void removeDialogRelatedData(Long chatId) {
        runtimeDialogManager.removePrincipalUser(chatId);
        Optional.ofNullable(activeDialogHandlers.remove(chatId))
                .ifPresent(handler -> handler.removeDialogProcessor(chatId));
    }

    private void bindDialogHandler(Long chatId, ButtonLabelKey buttonLabelKey) {
        existingDialogHandlers.stream()
                .filter(handler -> handler.belongToRootMenuButtons(buttonLabelKey))
                .forEach(dialogHandler -> {
                    dialogHandler.createDialogProcessor(chatId, buttonLabelKey);
                    activeDialogHandlers.put(chatId, dialogHandler);
                });
    }
}
