package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.DialogHandler;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.dialogs.SubDialogHandler;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DialogRouterServiceImpl implements DialogRouterService {

    private final Map<Long, DialogHandler> dialogHandlers;
    private final Map<Long, User> principalUsers;
    private final List<DialogHandler> existingDialogHandlers;

    private final DialogHandler generalDialogHandler;
    private final DialogHandler managerDialogHandler;
    private final DialogHandler adminDialogHandler;

    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public DialogRouterServiceImpl(@Qualifier("GeneralDialogHandler") DialogHandler generalDialogHandler,
                                   @Qualifier("ManagerDialogHandler") DialogHandler managerDialogHandler,
                                   @Qualifier("AdminDialogHandler") DialogHandler adminDialogHandler,
                                   SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {

        this.generalDialogHandler = generalDialogHandler;
        this.managerDialogHandler = managerDialogHandler;
        this.adminDialogHandler = adminDialogHandler;

        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;

        existingDialogHandlers = List.of(generalDialogHandler, managerDialogHandler, adminDialogHandler);
        dialogHandlers = new ConcurrentHashMap<>();
        principalUsers = new ConcurrentHashMap<>();
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        Long chatId = TelegramUtils.currentChatId(update);

        Optional<ButtonValue> optionalButtonValue = ButtonValue.getByText(input);

        if (optionalButtonValue.isPresent()) {
            ButtonValue buttonValue = optionalButtonValue.get();

            // return to root menu when click 'main menu' button
            if (ButtonValue.RETURN_MAIN_MENU.equals(buttonValue)) {
                startFlow(chatId);
                return;
            }

            // bind handlers when buttonValue contains name of particular dialog
            // if user doesn't exist go to startFlow on next condition
            if (!dialogHandlers.containsKey(chatId) && principalUsers.get(chatId) != null && !principalUsers.get(chatId).isDeleted()) {
                bindDialogHandler(chatId, buttonValue);
            }

            // when dialog in telegram remained on some step (different from starter) with buttons but app was reloaded
            // that means that there is no handler for the dialog - start from root menu
            if (!dialogHandlers.containsKey(chatId)) {
                sendBotMessageService.sendMessage(chatId, Message.GENERAL_ERROR_MESSAGE.text());
                startFlow(chatId);
                return;
            }
        }

        // when dialog in telegram remain on some step with user input but app was reloaded
        // that means that is no handler for the dialog - start from root menu
        if (!dialogHandlers.containsKey(chatId)) {
            sendBotMessageService.sendMessage(chatId, Message.GENERAL_ERROR_MESSAGE.text());
            startFlow(chatId);
            return;
        }
        dialogHandlers.get(chatId).handleTelegramInput(chatId, input);
    }

    @Override
    public void startFlow(Long chatId) {
        removeUnusedHandlers(chatId);
        User user = getDialogPrincipalUser(chatId);

        final String startFlowMessage = """
                Окей %s.
                Выбери диалог.
                """.formatted(user.getName());

        KeyboardRow[] keyboardRows = existingDialogHandlers.stream()
                .filter(handler -> checkDialogAccessibility(handler, user))
                .map(DialogHandler::getRootMenuButtons)
                .flatMap(Collection::stream)
                .toArray(KeyboardRow[]::new);
        SendMessage sendMessage = new SendMessage(chatId.toString(), startFlowMessage);
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(false, keyboardRows));
    }

    private boolean checkDialogAccessibility(DialogHandler handler, User user) {
        return !Collections.disjoint(handler.roleAccessibility(), user.getRoles());
    }

    private User getDialogPrincipalUser(Long chatId) {
        Optional<User> user = telegramUserService.findByChatId(chatId);
        if (user.isEmpty() || user.get().isDeleted()) {
            String reason = user.isEmpty() ? "Твоя учетная запись не найдена" : "Твоя учетная запись удалена";
            sendBotMessageService.sendMessage(chatId, "Похоже у тебя нет доступа к боту. Причина: %s. Свяжись с тем кто может обновить твою учетную запись!".formatted(reason));
            principalUsers.remove(chatId);
            throw new TelegramUserException("User is not available to set his as principal. ChatId: %s. User: %s".formatted(chatId, user));
        }
        principalUsers.put(chatId, user.get());
        return user.get();
    }

    private void removeUnusedHandlers(Long chatId) {
        Optional<DialogHandler> optionalHandler = Optional.ofNullable(dialogHandlers.get(chatId));
        optionalHandler.ifPresent(handler -> handler.removeStateMachineHandler(chatId));
        dialogHandlers.remove(chatId);
    }

    private void bindDialogHandler(Long chatId, ButtonValue buttonValue) {
        if (generalDialogHandler.belongToDialogStarter(buttonValue)) {
            generalDialogHandler.createStateMachineHandler(chatId, buttonValue);
            dialogHandlers.put(chatId, generalDialogHandler);
            return;
        }
        if (managerDialogHandler.belongToDialogStarter(buttonValue)) {
            dialogHandlers.put(chatId, managerDialogHandler);
            ((SubDialogHandler) managerDialogHandler).startSubDialogFlow(chatId);
            return;
        }
        if (adminDialogHandler.belongToDialogStarter(buttonValue)) {
            dialogHandlers.put(chatId, adminDialogHandler);
            ((SubDialogHandler) adminDialogHandler).startSubDialogFlow(chatId);
        }
    }
}
