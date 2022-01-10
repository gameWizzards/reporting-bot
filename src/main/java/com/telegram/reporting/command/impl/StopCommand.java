package com.telegram.reporting.command.impl;

import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.telegram.reporting.command.CommandUtils.getChatId;

/**
 * Stop {@link Command}.
 */
public non-sealed class StopCommand implements Command {

    public static final String STOP_MESSAGE = "Деактивировал все твои подписки \uD83D\uDE1F.\n" +
            "Ты всегда можешь вернуться нажав /start";
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public StopCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String alias() {
        return "/stop";
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(getChatId(update), STOP_MESSAGE);
        telegramUserService.findByChatId(getChatId(update))
                .ifPresent(it -> {
                    telegramUserService.save(it);
                });
    }
}
