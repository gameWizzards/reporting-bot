package com.telegram.reporting.bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    String COMMAND_PREFIX = "/";

    String alias();

    void execute(Update update);
}
