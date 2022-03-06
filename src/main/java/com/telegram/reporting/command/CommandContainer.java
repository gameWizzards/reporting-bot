package com.telegram.reporting.command;

import com.telegram.reporting.command.annotation.AdminCommand;
import com.telegram.reporting.command.impl.Command;
import com.telegram.reporting.command.impl.StartCommand;
import com.telegram.reporting.command.impl.AddUserCommand;
import com.telegram.reporting.command.impl.UnknownCommand;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import org.springframework.beans.factory.annotation.Value;

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
    @Value("${bot.admins}")
    private List<String> admins;
    private final Map<String, Command> commandMap;
    private final Command unknownCommand;

    public CommandContainer(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        commandMap = Stream.of(
                new StartCommand(sendBotMessageService, telegramUserService),
                new AddUserCommand(sendBotMessageService, telegramUserService)
        ).collect(Collectors.toUnmodifiableMap(Command::alias, Function.identity()));

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
