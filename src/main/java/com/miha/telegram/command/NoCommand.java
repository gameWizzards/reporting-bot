package com.miha.telegram.command;

import com.miha.telegram.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.miha.telegram.command.CommandUtils.getChatId;

/**
 * No {@link Command}.
 */
public class NoCommand implements Command {

    public static final String NO_MESSAGE = "Я поддерживаю команды, начинающиеся со слеша(/).\n"
            + "Чтобы посмотреть список комманд введи /help";
    private final SendBotMessageService sendBotMessageService;

    public NoCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(getChatId(update), NO_MESSAGE);
    }
}
