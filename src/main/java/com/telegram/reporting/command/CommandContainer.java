package com.telegram.reporting.command;

import com.telegram.reporting.command.annotation.AdminCommand;
import com.telegram.reporting.command.impl.Command;
import com.telegram.reporting.command.impl.HelpCommand;
import com.telegram.reporting.command.impl.NoCommand;
import com.telegram.reporting.command.impl.StartCommand;
import com.telegram.reporting.command.impl.StopCommand;
import com.telegram.reporting.command.impl.UnknownCommand;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

/**
 * Container of the {@link Command}s, which are using for handling telegram commands.
 */
public class CommandContainer {

    private final Map<String, Command> commandMap;
    private final Command unknownCommand;
    private final Command noCommand;
    private final List<String> admins;

    public CommandContainer(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService, List<String> admins) {
        this.admins = admins;

        commandMap = Stream.of(
                new StartCommand(sendBotMessageService, telegramUserService),
                new StopCommand(sendBotMessageService, telegramUserService),
                new HelpCommand(sendBotMessageService)
        ).collect(Collectors.toUnmodifiableMap(Command::alias, Function.identity()));

        unknownCommand = new UnknownCommand(sendBotMessageService);
        noCommand = new NoCommand(sendBotMessageService);
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

    public Command noCommand(String username) {
        return noCommand;
    }

    private boolean isAdminCommand(Command command) {
        return nonNull(command.getClass().getAnnotation(AdminCommand.class));
    }
}
