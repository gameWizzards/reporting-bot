package com.telegram.reporting.command;

import com.telegram.reporting.command.impl.Command;
import com.telegram.reporting.command.impl.StartCommand;
import org.junit.jupiter.api.DisplayName;

import static com.telegram.reporting.command.impl.StartCommand.START_MESSAGE;

@DisplayName("Unit-level testing for StartCommand")
class StartCommandTest extends AbstractCommandTest {

    @Override
    String getCommandName() {
        return getCommand().alias();
    }

    @Override
    String getCommandMessage() {
        return START_MESSAGE;
    }

    @Override
    Command getCommand() {
        return new StartCommand(sendBotMessageService, telegramUserService);
    }
}