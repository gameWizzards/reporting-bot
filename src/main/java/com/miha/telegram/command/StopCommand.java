package com.miha.telegram.command;

import com.miha.telegram.service.SendBotMessageService;
import com.miha.telegram.service.TelegramUserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.miha.telegram.command.CommandUtils.getChatId;

/**
 * Stop {@link Command}.
 */
public class StopCommand implements Command {

    public static final String STOP_MESSAGE = "Деактивировал все твои подписки \uD83D\uDE1F.\n" +
            "Ты всегда можешь вернуться нажав /start";
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public StopCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(getChatId(update), STOP_MESSAGE);
        telegramUserService.findByChatId(getChatId(update))
                .ifPresent(it -> {
                    it.setActive(false);
                    telegramUserService.save(it);
                });
    }
}
