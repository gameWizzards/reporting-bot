package com.telegram.reporting.bot;

import com.telegram.reporting.command.CommandContainer;
import com.telegram.reporting.command.impl.StartCommand;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.DialogRouterService;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
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
    private final TelegramUserService telegramUserService;
    private final CommandContainer commandContainer;

    public ReportingTelegramBot(@Lazy DialogRouterService dialogRouterService, TelegramUserService telegramUserService, CommandContainer commandContainer) {
        this.dialogRouterService = dialogRouterService;
        this.telegramUserService = telegramUserService;
        this.commandContainer = commandContainer;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().hasContact()) {
            User user = telegramUserService.verifyContact(update.getMessage());
            if (user == null) {
                throw new RuntimeException("TODO");
            }
            dialogRouterService.startFlow(user);
            return;
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = TelegramUtils.getMessage(update);

            if (message.startsWith(TelegramUtils.COMMAND_PREFIX)) {
                commandContainer.findCommand(TelegramUtils.getCommandIdentifier(message), update.getMessage().getFrom().getUserName()).execute(update);
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

}
