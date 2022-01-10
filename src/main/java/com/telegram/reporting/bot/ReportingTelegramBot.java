package com.telegram.reporting.bot;

import com.telegram.reporting.command.CommandContainer;
import com.telegram.reporting.command.CommandName;
import com.telegram.reporting.service.SendBotMessageServiceImpl;
import com.telegram.reporting.service.TelegramUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class ReportingTelegramBot extends TelegramLongPollingBot {

    public static String COMMAND_PREFIX = "/";
    private final CommandContainer commandContainer;
    @Value("#{'${bot.admins}'.split(',')}")
    List<String> admins;
    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    @Autowired
    public ReportingTelegramBot(TelegramUserService telegramUserService) {
        this.commandContainer = new CommandContainer(
                new SendBotMessageServiceImpl(this), telegramUserService, admins
        );
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            String username = update.getMessage().getFrom().getUserName();
            if (message.startsWith(COMMAND_PREFIX)) {
                String commandIdentifier = getCommandIdentifier(message);
                commandContainer.findCommand(commandIdentifier, username).execute(update);
            } else {
                commandContainer.findCommand(CommandName.NO.getCommandName(), username).execute(update);
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