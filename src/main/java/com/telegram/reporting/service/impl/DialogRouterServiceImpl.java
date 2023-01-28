package com.telegram.reporting.service.impl;

import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.I18nButtonService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.RuntimeDialogManager;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogRouterServiceImpl implements DialogRouterService {

    private final Map<Long, DialogHandler> activeDialogHandlers = new ConcurrentHashMap<>();

    private final List<DialogHandler> existingDialogHandlers;
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;
    private final RuntimeDialogManager runtimeDialogManager;
    private final I18nMessageService i18NMessageService;
    private final I18nButtonService i18nButtonService;

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        Long chatId = CommonUtils.currentChatId(update);

        // handle simple button, contact sharing
        if (CommonUtils.hasContact(update)) {
            sendBotMessageService.removeReplyKeyboard(chatId, i18NMessageService.getMessage(chatId, MessageKey.PD_SUCCESS_CONTACT_SHARING));
            try {
                User user = telegramUserService.verifyContact(update.getMessage());
                startFlow(user.getChatId(), user.getLocale());

            } catch (TelegramUserException e) {
                log.error("Can't verify contact! Reason: {}", e.getMessage(), e);
                sendBotMessageService.sendMessage(chatId, i18NMessageService.getMessage(chatId, MessageKey.PD_FAILED_CONTACT_CHECKING));
            }
            return;
        }

        Locale principalUserLocale = runtimeDialogManager.getPrincipalUserLocale(chatId);
        // handle InlineMarkup button
        if (CommonUtils.isInlineButton(update)) {
            String buttonCallbackData = CommonUtils.getButtonCallbackData(update);
            ButtonLabelKey buttonLabelKey = ButtonLabelKey.getByKey(buttonCallbackData);

            // handle dynamic ordinal buttons which don't have mapping on ButtonLabelKey
            if (Objects.isNull(buttonLabelKey) && CommonUtils.isDynamicOrdinalInlineButton(buttonCallbackData)) {
                activeDialogHandlers.get(chatId).handleTelegramUserInput(chatId, buttonCallbackData);
                return;
            }

            // return to root menu when click 'main menu' button
            if (ButtonLabelKey.COMMON_RETURN_MAIN_MENU.equals(buttonLabelKey)) {
                startFlow(chatId, principalUserLocale);
                return;
            }

            // bind handlers when buttonValue contains name of particular dialog
            // if user doesn't exist go to startFlow on next condition
            if (!activeDialogHandlers.containsKey(chatId) && isAllowStartDialog(chatId)) {
                bindDialogHandler(chatId, buttonLabelKey);
            }

            // TODO refactor this case!! It's not necessary to have only this option
            // when dialog in telegram remained on some step (different from starter) with buttons but app was reloaded
            // that means that there is no handler for the dialog - start from root menu
            if (!activeDialogHandlers.containsKey(chatId)) {
                sendBotMessageService.sendMessage(chatId,
                        i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_SOMETHING_GOING_WRONG));
                startFlow(chatId, principalUserLocale);
                return;
            }

            activeDialogHandlers.get(chatId).handleInlineButtonInput(chatId, buttonLabelKey);
            return;
        }

        //TODO refactor this case!! It's not necessary to have only this option
        // when dialog in telegram remain on some step with user input but app was reloaded
        // that means that is no handler for the dialog - start from root menu
        if (!activeDialogHandlers.containsKey(chatId)) {
            log.error("Can't find appropriate active handler to chat [{}].", chatId);
            sendBotMessageService.sendMessage(chatId,
                    i18NMessageService.getMessage(chatId, MessageKey.COMMON_WARNING_SOMETHING_GOING_WRONG));
            startFlow(chatId, principalUserLocale);
            return;
        }

        String input = CommonUtils.getMessageText(update);
        activeDialogHandlers.get(chatId).handleTelegramUserInput(chatId, input);
    }

    @Override
    public void startFlow(Long chatId, Locale locale) {
        removeDialogRelatedData(chatId);
        User user = runtimeDialogManager.getPrincipalUser(chatId);

        final String startFlowMessage = i18NMessageService.getMessage(
                chatId,
                MessageKey.PD_START_FLOW_MESSAGE,
                user.getName());

        List<List<InlineKeyboardButton>> buttonRows = i18nButtonService.getRootMenuButtons(chatId, user);

        SendMessage sendMessage = new SendMessage(chatId.toString(), startFlowMessage);
        sendBotMessageService.sendMessageWithKeys(sendMessage, new InlineKeyboardMarkup(buttonRows));
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
