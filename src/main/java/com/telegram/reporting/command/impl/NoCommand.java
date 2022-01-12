package com.telegram.reporting.command.impl;

import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.CommandUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * No {@link Command}.
 */
public non-sealed class NoCommand implements Command {

    public static final String NO_MESSAGE = "test";

    private final SendBotMessageService sendBotMessageService;

    public NoCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public String alias() {
        return "noCommand";
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(CommandUtils.getChatId(update), NO_MESSAGE);
    }
}
