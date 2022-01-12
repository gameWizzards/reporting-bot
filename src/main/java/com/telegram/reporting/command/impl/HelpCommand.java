package com.telegram.reporting.command.impl;

import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.utils.CommandUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Help {@link Command}.
 */
public non-sealed class HelpCommand implements Command {

    public static final String HELP_MESSAGE = """
            test
            test
            """;

    private final SendBotMessageService sendBotMessageService;

    public HelpCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public String alias() {
        return "/help";
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(CommandUtils.getChatId(update), HELP_MESSAGE);
    }
}
