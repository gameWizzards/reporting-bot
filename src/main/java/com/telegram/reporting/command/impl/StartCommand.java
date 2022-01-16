package com.telegram.reporting.command.impl;

import com.telegram.reporting.messages.Message;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.TelegramUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.List;

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
        message.setChatId(TelegramUtils.getChatId(update).toString());
        message.setText(START_MESSAGE);
        message.setReplyMarkup(getReplyKeyboardMarkup());

        sendBotMessageService.sendMessage(message);
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(getKeyboards());
        return replyKeyboardMarkup;
    }

    private List<KeyboardRow> getKeyboards() {
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(Message.CREATE_REPORT.text());

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.addAll(Arrays.asList(Message.UPDATE_REPORT.text(), Message.DELETE_REPORT.text()));

        return Arrays.asList(firstRow, secondRow);
    }
}
