package com.telegram.reporting.bot.configuration;

import com.telegram.reporting.command.CommandContainer;
import com.telegram.reporting.command.impl.AddUserCommand;
import com.telegram.reporting.command.impl.StartCommand;
import com.telegram.reporting.command.impl.UnknownCommand;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Configuration
public class CommandsConfiguration {

    @Bean
    public AddUserCommand addUserCommand(@Lazy SendBotMessageService sendBotMessageService) {
        return new AddUserCommand(sendBotMessageService);
    }

    @Bean
    public StartCommand startCommand(@Lazy SendBotMessageService sendBotMessageService) {
        return new StartCommand(sendBotMessageService);
    }

}
