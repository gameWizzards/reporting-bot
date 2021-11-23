package com.miha.telegram.command;

import com.miha.telegram.repository.entity.TelegramUser;
import com.miha.telegram.service.SendBotMessageService;
import com.miha.telegram.service.TelegramUserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.miha.telegram.command.CommandUtils.getChatId;

/**
 * Start {@link Command}.
 */
public class StartCommand implements Command {

    public final static String START_MESSAGE = "Привет. Я Miha Telegram Bot.\n" +
            "Не знаешь о чем я? Напиши /help, чтобы узнать что я умею.";
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public StartCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(Update update) {
        Long chatId = getChatId(update);

        telegramUserService.findByChatId(chatId).ifPresentOrElse(
                user -> {
                    user.setActive(true);
                    telegramUserService.save(user);
                },
                () -> {
                    TelegramUser telegramUser = new TelegramUser();
                    telegramUser.setActive(true);
                    telegramUser.setChatId(chatId);
                    telegramUserService.save(telegramUser);
                });

        sendBotMessageService.sendMessage(chatId, START_MESSAGE);
    }
}
