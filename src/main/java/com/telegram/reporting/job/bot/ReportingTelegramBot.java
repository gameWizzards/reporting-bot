package com.telegram.reporting.job.bot;

import com.telegram.reporting.command.CommandContainer;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.impl.SendBotMessageServiceImpl;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.TelegramUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class ReportingTelegramBot extends TelegramLongPollingBot {

    @Value("#{'${bot.admins}'.split(',')}")
    List<String> admins;
    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    private final CommandContainer commandContainer;
    private final DialogRouterService dialogRouterService;

    @Autowired
    public ReportingTelegramBot(TelegramUserService telegramUserService, DialogRouterService dialogRouterService) {
        this.dialogRouterService = dialogRouterService;
        this.commandContainer = new CommandContainer(
                new SendBotMessageServiceImpl(this), telegramUserService, admins
        );
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            String username = update.getMessage().getFrom().getUserName();
            if (message.startsWith(TelegramUtils.COMMAND_PREFIX)) {
                String commandIdentifier = getCommandIdentifier(message);
                commandContainer.findCommand(commandIdentifier, username).execute(update);
            } else {
                // buttons or user input
                // TODO implement router for all dialogs
                dialogRouterService.handleTelegramUpdateEvent(update);
            }
        } else if (update.hasCallbackQuery()) {
            //TODO
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
