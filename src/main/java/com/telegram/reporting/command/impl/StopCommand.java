package com.telegram.reporting.command.impl;

import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.TelegramUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Stop {@link Command}.
 */
public non-sealed class StopCommand implements Command {

    public static final String STOP_MESSAGE = """
            test
            test
            """;

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
        sendBotMessageService.sendMessage(TelegramUtils.currentChatId(update), STOP_MESSAGE);
        telegramUserService.findByChatId(TelegramUtils.currentChatId(update))
                .ifPresent(telegramUserService::save);
    }
}
