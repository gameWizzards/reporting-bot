package com.telegram.reporting.command.impl;

import com.telegram.reporting.command.Command;
import com.telegram.reporting.command.CommandName;
import com.telegram.reporting.command.CommandUtils;
import com.telegram.reporting.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Help {@link Command}.
 */
public class HelpCommand implements Command {

    public static final String HELP_MESSAGE = String.format("✨Дотупные команды✨\n\n"
                    + "%s - начать работу со мной\n"
                    + "%s - приостановить работу со мной\n"
                    + "%s - получить помощь в работе со мной\n",
            CommandName.START.getCommandName(),
            CommandName.STOP.getCommandName(),
            CommandName.HELP.getCommandName());
    private final SendBotMessageService sendBotMessageService;

    public HelpCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(CommandUtils.getChatId(update), HELP_MESSAGE);
    }
}
