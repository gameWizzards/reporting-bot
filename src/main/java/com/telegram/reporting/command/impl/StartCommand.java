package com.telegram.reporting.command.impl;

import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.KeyboardUtils;
import com.telegram.reporting.utils.TelegramUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

/**
 * Start {@link Command}.
 */
public non-sealed class StartCommand implements Command {

    public final static String START_MESSAGE = """
            Добро пожаловать!
            Нажмите на кнопку ниже чтобы верифицировать вас как сотрудника.
            """;

    private final SendBotMessageService sendBotMessageService;

    public StartCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public String alias() {
        return "/start";
    }

    @Override
    public void execute(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(TelegramUtils.currentChatId(update).toString());
        message.setText(START_MESSAGE);

        KeyboardRow shareContact = KeyboardUtils.createButton("Поделится номером телефона");
        shareContact.get(0).setRequestContact(true);
        sendBotMessageService.sendMessageWithKeys(message, KeyboardUtils.createKeyboardMarkup(shareContact));
    }

}
