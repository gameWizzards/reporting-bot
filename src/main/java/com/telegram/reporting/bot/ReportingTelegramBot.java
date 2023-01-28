package com.telegram.reporting.bot;

import com.telegram.reporting.bot.command.CommandContainer;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.utils.CommonUtils;
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

    public ReportingTelegramBot(@Lazy DialogRouterService dialogRouterService, CommandContainer commandContainer) {
        this.dialogRouterService = dialogRouterService;
        this.commandContainer = commandContainer;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (CommonUtils.hasMessageText(update) && CommonUtils.isTelegramCommand(update)) {
            commandContainer.findCommand(CommonUtils.getMessageText(update)).execute(update);
            return;
        }
        // TODO change Update object to Some Adapter(interface with 3 impl - user input, simple button, inline button)
        dialogRouterService.handleTelegramUpdateEvent(update);
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
