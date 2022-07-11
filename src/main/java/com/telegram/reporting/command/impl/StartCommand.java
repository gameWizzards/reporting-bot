package com.telegram.reporting.command.impl;

import com.telegram.reporting.dialogs.ButtonValue;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Optional;

/**
 * Start {@link Command}.
 */
public non-sealed class StartCommand implements Command {

    public final static String START_MESSAGE = """
            Привет!
            Для того чтобы начать диалог тебе нужно поделиться своим номером телефона. Жми на кнопку ниже "Поделится номером телефона".
            """;

    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;
    private final DialogRouterService dialogRouterService;

    public StartCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService,
                        DialogRouterService dialogRouterService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
        this.dialogRouterService = dialogRouterService;
    }

    @Override
    public String alias() {
        return "/start";
    }

    @Override
    public void execute(Update update) {
        Long chatId = TelegramUtils.currentChatId(update);
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(START_MESSAGE);

        Optional<User> user = telegramUserService.findByChatId(chatId);

        if (user.isEmpty()) {
            KeyboardRow shareContact = KeyboardUtils.createButton(ButtonValue.SHARE_PHONE.text());
            shareContact.get(0).setRequestContact(true);
            sendBotMessageService.sendMessageWithKeys(message, KeyboardUtils.createKeyboardMarkup(false, shareContact));
        } else {
            dialogRouterService.startFlow(chatId);
        }
    }
}
