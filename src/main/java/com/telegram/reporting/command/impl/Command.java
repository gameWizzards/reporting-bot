package com.telegram.reporting.command.impl;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Command interface for handling telegram-bot commands.
 */
public sealed interface Command
        permits HelpCommand, NoCommand, StartCommand, StopCommand, UnknownCommand {

    String alias();

    /**
     * Main method, which is executing command logic.
     *
     * @param update provided {@link Update} object with all the needed data for command.
     */
    void execute(Update update);

    default void handle(Update update, String username) {
    }

    ;
}
