package com.telegram.reporting.command.impl;

import com.telegram.reporting.command.impl.AddUserCommand;
import com.telegram.reporting.command.impl.StartCommand;
import com.telegram.reporting.command.impl.UnknownCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Command interface for handling telegram-bot commands.
 */
public sealed interface Command
        permits StartCommand, AddUserCommand, UnknownCommand {

    String alias();

    /**
     * Main method, which is executing command logic.
     *
     * @param update provided {@link Update} object with all the needed data for command.
     */
    void execute(Update update);
}
