package com.telegram.reporting.bot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MessageConfiguration.class)
public class CommandsConfiguration {

}
