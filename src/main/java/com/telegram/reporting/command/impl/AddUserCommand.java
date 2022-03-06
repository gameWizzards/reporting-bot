package com.telegram.reporting.command.impl;

import com.telegram.reporting.command.annotation.AdminCommand;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.TelegramUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Stop {@link Command}.
 */
@AdminCommand
public non-sealed class AddUserCommand implements Command {

    public static final String ADD_USER_MESSAGE = """
            Введите номер телефона сотрудника.
            Для нескольких номеров перечислите их через запятую.
            """;

    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public AddUserCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String alias() {
        return "/adduser";
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(update), ADD_USER_MESSAGE);
    }
}
