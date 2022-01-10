package com.telegram.reporting.command;

import com.telegram.reporting.command.impl.Command;
import com.telegram.reporting.command.impl.NoCommand;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Unit-level testing for NoCommand")
public class NoCommandTest extends AbstractCommandTest {

    @Override
    String getCommandName() {
        return getCommand().alias();
    }

    @Override
    String getCommandMessage() {
        return NoCommand.NO_MESSAGE;
    }

    @Override
    Command getCommand() {
        return new NoCommand(sendBotMessageService);
    }
}
