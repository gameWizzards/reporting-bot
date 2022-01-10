package com.telegram.reporting.command;

import com.telegram.reporting.command.impl.Command;
import com.telegram.reporting.command.impl.HelpCommand;
import org.junit.jupiter.api.DisplayName;

import static com.telegram.reporting.command.impl.HelpCommand.HELP_MESSAGE;

@DisplayName("Unit-level testing for HelpCommand")
public class HelpCommandTest extends AbstractCommandTest {


    @Override
    String getCommandName() {
        return getCommand().alias();
    }

    @Override
    String getCommandMessage() {
        return HELP_MESSAGE;
    }

    @Override
    Command getCommand() {
        return new HelpCommand(sendBotMessageService);
    }
}
