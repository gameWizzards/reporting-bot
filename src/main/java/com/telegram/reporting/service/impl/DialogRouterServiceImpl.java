package com.telegram.reporting.service.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.dialogs.Message;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.DialogHandler;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.SubDialogHandler;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Slf4j
@Service
public class DialogRouterServiceImpl implements DialogRouterService {

    private final Map<String, DialogHandler> dialogHandlers;
    private final List<DialogHandler> existingDialogHandlers;

    private final DialogHandler generalDialogHandler;
    private final DialogHandler managerDialogHandler;
    private final DialogHandler adminDialogHandler;

    private final SendBotMessageService sendBotMessageService;

    public DialogRouterServiceImpl(@Qualifier("GeneralDialogHandler") DialogHandler generalDialogHandler,
                                   @Qualifier("ManagerDialogHandler") DialogHandler managerDialogHandler,
                                   @Qualifier("AdminDialogHandler") DialogHandler adminDialogHandler,
                                   SendBotMessageService sendBotMessageService) {

        this.sendBotMessageService = sendBotMessageService;

        this.generalDialogHandler = generalDialogHandler;
        this.managerDialogHandler = managerDialogHandler;
        this.adminDialogHandler = adminDialogHandler;

        existingDialogHandlers = List.of(generalDialogHandler, managerDialogHandler, adminDialogHandler);
        dialogHandlers = new ConcurrentHashMap<>();
    }

    @Override
    public void handleTelegramUpdateEvent(Update update) {
        String input = TelegramUtils.getMessage(update);
        String chatId = TelegramUtils.currentChatId(update).toString();

        Optional<ButtonValue> optionalButtonValue = ButtonValue.getByText(input);

        if (optionalButtonValue.isPresent()) {
            ButtonValue buttonValue = optionalButtonValue.get();

            // return to root menu when click 'main menu' button
            if (ButtonValue.MAIN_MENU.equals(buttonValue)) {
                removeUnusedHandlers(chatId);
                startFlow(chatId);
                return;
            }

            // bind handlers when buttonValue contains name of particular dialog
            //TODO consider the possibility to add checking - if handler bound already
            bindDialogHandler(chatId, buttonValue);
            bindSubDialogHandler(chatId, buttonValue);

            // when dialog in telegram remained on some step with buttons but app was reloaded
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
    public void startFlow(String chatId) {
        removeUnusedHandlers(chatId);
        final String startFlowMessage = """
                Окей.
                Выбери диалог.
                """;
        KeyboardRow[] keyboardRows = existingDialogHandlers.stream()
                .map(handler -> handler.getRootMenuButtons(chatId))
                .flatMap(Collection::stream)
                .filter(Predicate.not(CollectionUtils::isEmpty))
                .toArray(KeyboardRow[]::new);
        SendMessage sendMessage = new SendMessage(chatId, startFlowMessage);
        sendBotMessageService.sendMessageWithKeys(sendMessage, KeyboardUtils.createKeyboardMarkup(false, keyboardRows));
    }

    private void removeUnusedHandlers(String chatId) {
        Optional<DialogHandler> optionalHandler = Optional.ofNullable(dialogHandlers.get(chatId));
        optionalHandler.ifPresent(handler -> handler.removeStateMachineHandler(chatId));
        dialogHandlers.remove(chatId);
    }

    private void bindDialogHandler(String chatId, ButtonValue buttonValue) {
        if (generalDialogHandler.belongToDialogStarter(buttonValue)) {
            generalDialogHandler.createStateMachineHandler(chatId, buttonValue);
            dialogHandlers.put(chatId, generalDialogHandler);
            return;
        }
        if (managerDialogHandler.belongToDialogStarter(buttonValue)) {
            dialogHandlers.put(chatId, managerDialogHandler);
            return;
        }
        if (adminDialogHandler.belongToDialogStarter(buttonValue)) {
            dialogHandlers.put(chatId, adminDialogHandler);
        }
    }

    private void bindSubDialogHandler(String chatId, ButtonValue buttonValue) {
        if (((SubDialogHandler) managerDialogHandler).belongToSubDialogStarter(buttonValue)) {
            dialogHandlers.get(chatId).createStateMachineHandler(chatId, buttonValue);
            return;
        }
        if (((SubDialogHandler) adminDialogHandler).belongToSubDialogStarter(buttonValue)) {
            dialogHandlers.get(chatId).createStateMachineHandler(chatId, buttonValue);
        }
    }
}
