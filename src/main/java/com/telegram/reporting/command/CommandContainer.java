package com.telegram.reporting.command;

import com.telegram.reporting.command.annotation.AdminCommand;
import com.telegram.reporting.command.impl.Command;
import com.telegram.reporting.command.impl.UnknownCommand;
import com.telegram.reporting.service.SendBotMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Container of the {@link Command}s, which are using for handling telegram commands.
 */
@Component
public class CommandContainer {
    @Value("${bot.admins}")
    private List<String> admins;
    private final Map<String, Command> commandMap;
    private final Command unknownCommand;

    public CommandContainer(@Autowired List<Command> commands, @Lazy SendBotMessageService sendBotMessageService) {
        commandMap = commands.stream()
                .collect(Collectors.toUnmodifiableMap(Command::alias, Function.identity()));

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
