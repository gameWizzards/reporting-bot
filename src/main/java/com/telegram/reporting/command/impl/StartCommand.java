package com.telegram.reporting.command.impl;

import com.telegram.reporting.messages.Message;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
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
            Окей.
            Давай создадим отчет за сегодня.
            """;

    private final SendBotMessageService sendBotMessageService;

    private final TelegramUserService telegramUserService;

    public StartCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
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
        KeyboardRow firstRow = KeyboardUtils.createButton(Message.CREATE_REPORT.text());
        KeyboardRow secondRow = KeyboardUtils.createRowButtons(Message.UPDATE_REPORT.text(), Message.DELETE_REPORT.text());

        sendBotMessageService.sendMessageWithKeys(message, KeyboardUtils.createKeyboardMarkup(firstRow, secondRow));
    }

}
