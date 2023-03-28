package com.telegram.reporting.bot.command;

import com.telegram.reporting.bot.event.CommandEvent;

public interface Command {
    String COMMAND_PREFIX = "/";

    String alias();

    void execute(CommandEvent event);
}
