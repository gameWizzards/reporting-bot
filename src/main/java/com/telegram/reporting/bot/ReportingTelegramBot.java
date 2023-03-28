package com.telegram.reporting.bot;

import com.telegram.reporting.bot.command.CommandContainer;
import com.telegram.reporting.bot.event.CommandEvent;
import com.telegram.reporting.bot.event.TelegramEvent;
import com.telegram.reporting.service.DialogRouterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class ReportingTelegramBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    private final DialogRouterService dialogRouterService;
    private final CommandContainer commandContainer;
    private final TelegramEventFactory eventFactory;

    public ReportingTelegramBot(@Lazy DialogRouterService dialogRouterService, CommandContainer commandContainer,
                                TelegramEventFactory eventFactory) {
        this.dialogRouterService = dialogRouterService;
        this.commandContainer = commandContainer;
        this.eventFactory = eventFactory;
    }

    @Override
    public void onUpdateReceived(Update update) {
        TelegramEvent event = eventFactory.getEvent(update);

        if (event instanceof CommandEvent commandEvent) {
            commandContainer.findCommand(commandEvent.command()).execute(commandEvent);
            return;
        }

        dialogRouterService.handleTelegramEvent(event);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}
