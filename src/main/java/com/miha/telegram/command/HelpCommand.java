package com.miha.telegram.command;

import com.miha.telegram.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.miha.telegram.command.CommandUtils.getChatId;

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
        sendBotMessageService.sendMessage(getChatId(update), HELP_MESSAGE);
    }
}
