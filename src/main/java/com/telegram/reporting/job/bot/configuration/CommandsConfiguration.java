package com.telegram.reporting.job.bot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MessageConfiguration.class)
public class CommandsConfiguration {

}
