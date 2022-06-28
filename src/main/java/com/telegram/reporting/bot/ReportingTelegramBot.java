package com.telegram.reporting.bot;

import com.telegram.reporting.command.CommandContainer;
import com.telegram.reporting.exception.TelegramUserException;
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
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;
    private final CommandContainer commandContainer;

    public ReportingTelegramBot(@Lazy DialogRouterService dialogRouterService, @Lazy SendBotMessageService sendBotMessageService,
                                TelegramUserService telegramUserService, CommandContainer commandContainer) {
        this.dialogRouterService = dialogRouterService;
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
        this.commandContainer = commandContainer;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().hasContact()) {
            User user = telegramUserService.verifyContact(update.getMessage());
            if (user == null) {
                sendBotMessageService.sendMessage(TelegramUtils.currentChatId(update), "Кажеться твой номер не добавили в список разрешенных. Свяжись с тем кто может добавить твой номер в White list!");
                throw new TelegramUserException(String.format("This user is not registered yet! Phone = +%s. ChatId = %s.", update.getMessage().getContact().getPhoneNumber(), TelegramUtils.currentChatId(update)));
            }
            dialogRouterService.startFlow(user.getChatId().toString());
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
