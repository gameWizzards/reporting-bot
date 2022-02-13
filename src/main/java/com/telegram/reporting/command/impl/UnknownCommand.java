package com.telegram.reporting.command.impl;

import com.telegram.reporting.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.telegram.reporting.utils.TelegramUtils.currentChatId;

/**
 * Unknown {@link Command}.
 */
public non-sealed class UnknownCommand implements Command {

    public static final String UNKNOWN_MESSAGE = """
            test
            test
            """;

    private final SendBotMessageService sendBotMessageService;

    public UnknownCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public String alias() {
        return "unknownCommand";
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(currentChatId(update), UNKNOWN_MESSAGE);
    }
}
