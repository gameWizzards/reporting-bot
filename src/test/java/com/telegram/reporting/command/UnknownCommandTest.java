package com.telegram.reporting.command;

import com.telegram.reporting.command.impl.Command;
import com.telegram.reporting.command.impl.UnknownCommand;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Unit-level testing for UnknownCommand")
public class UnknownCommandTest extends AbstractCommandTest {

    @Override
    String getCommandName() {
        return "/fdgdfgdfgdbd";
    }

    @Override
    String getCommandMessage() {
        return UnknownCommand.UNKNOWN_MESSAGE;
    }

    @Override
    Command getCommand() {
        return new UnknownCommand(sendBotMessageService);
    }
}
