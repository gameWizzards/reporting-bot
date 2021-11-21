package com.miha.telegram.command;

import com.google.common.collect.ImmutableMap;
import com.miha.telegram.command.annotation.AdminCommand;
import com.miha.telegram.service.SendBotMessageService;
import com.miha.telegram.service.TelegramUserService;

import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Container of the {@link Command}s, which are using for handling telegram commands.
 */
public class CommandContainer {

    private final ImmutableMap<String, Command> commandMap;
    private final Command unknownCommand;
    private final List<String> admins;

    public CommandContainer(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService, List<String> admins) {

        this.admins = admins;
        commandMap = ImmutableMap.<String, Command>builder()
                .put(CommandName.START.getCommandName(), new StartCommand(sendBotMessageService, telegramUserService))
                .put(CommandName.STOP.getCommandName(), new StopCommand(sendBotMessageService, telegramUserService))
                .put(CommandName.HELP.getCommandName(), new HelpCommand(sendBotMessageService))
                .put(CommandName.NO.getCommandName(), new NoCommand(sendBotMessageService))
                .build();

        unknownCommand = new UnknownCommand(sendBotMessageService);
    }

    public Command findCommand(String commandIdentifier, String username) {
        Command command = commandMap.getOrDefault(commandIdentifier, unknownCommand);
        if (isAdminCommand(command)) {
            if (admins.contains(username)) {
                return command;
            } else {
                return unknownCommand;
            }
        }
        return command;
    }

    private boolean isAdminCommand(Command command) {
        return nonNull(command.getClass().getAnnotation(AdminCommand.class));
    }
}
