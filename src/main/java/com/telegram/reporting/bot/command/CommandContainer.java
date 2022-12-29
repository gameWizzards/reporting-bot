package com.telegram.reporting.bot.command;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommandContainer {
    private final Map<String, Command> commandMap;
    private final Command unknownCommand;

    public CommandContainer(@Qualifier("UnknownCommand") Command unknownCommand, List<Command> commands) {
        this.unknownCommand = unknownCommand;
        commandMap = commands.stream()
                .collect(Collectors.toUnmodifiableMap(Command::alias, Function.identity()));
    }

    public Command findCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, unknownCommand);
    }
}
