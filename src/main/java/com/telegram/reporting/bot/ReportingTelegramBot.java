package com.telegram.reporting.bot;

import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.utils.TelegramUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ReportingTelegramBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    private final DialogRouterService dialogRouterService;

    public ReportingTelegramBot(@Lazy DialogRouterService dialogRouterService) {
        this.dialogRouterService = dialogRouterService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            String username = update.getMessage().getFrom().getUserName();
            if (message.startsWith(TelegramUtils.COMMAND_PREFIX)) {
                String commandIdentifier = getCommandIdentifier(message);
                dialogRouterService.handleBeginningBotDialog(commandIdentifier, username, update);
//                commandContainer.findCommand(commandIdentifier, username).execute(update);
            } else {
                dialogRouterService.handleTelegramUpdateEvent(update);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private String getCommandIdentifier(String message) {
        return message.split(" ")[0].toLowerCase();
    }

}
