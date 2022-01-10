package com.telegram.reporting.command.impl;

import com.telegram.reporting.command.CommandUtils;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Start {@link Command}.
 */
public non-sealed class StartCommand implements Command {

    public final static String START_MESSAGE = "Привет. Я Miha Telegram Bot.\n" +
            "Не знаешь о чем я? Напиши /help, чтобы узнать что я умею.";
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
        Long chatId = CommandUtils.getChatId(update);

        telegramUserService.findByChatId(chatId).ifPresentOrElse(telegramUserService::save,
                () -> {
                    User user = new User();
                    user.setChatId(chatId);
                    telegramUserService.save(user);
                });

        sendBotMessageService.sendMessage(chatId, START_MESSAGE);
    }
}
