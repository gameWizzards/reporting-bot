package com.telegram.reporting.command.impl;

import com.telegram.reporting.command.CommandUtils;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Start {@link Command}.
 */
public non-sealed class StartCommand implements Command {

    public final static String createReportText = "Создать отчет";
    public final static String updateReportText = "Редактировать отчет";
    public final static String removeReportText = "Удалить отчет";

    public final static String START_MESSAGE = """
            test
            test
            test
            """;

    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public StartCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String alias() {
        return "/start";
    }

    public static List<String> availableTexts() {
        return Arrays.asList(createReportText, updateReportText, removeReportText);
    }

    @Override
    public void execute(Update update) {
        Long chatId = CommandUtils.getChatId(update);

//        telegramUserService.findByChatId(chatId).ifPresentOrElse(telegramUserService::save,
//                () -> {
//                    User user = new User();
//                    user.setChatId(chatId);
//                    telegramUserService.save(user);
//                });

//        SendMessage message = new SendMessage(); // Create a message object object
//        message.setChatId(chatId.toString());
//        message.setText("You send /start");
//
//        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
//        List<InlineKeyboardButton> rowInline = new ArrayList<>();
//
//
//        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//        inlineKeyboardButton.setText("Update message text");
//        inlineKeyboardButton.setCallbackData("update_msg_text");
//
//        rowInline.add(inlineKeyboardButton);
//        // Set the keyboard to the markup
//        rowsInline.add(rowInline);
//        // Add it to the message
//        markupInline.setKeyboard(rowsInline);
//        message.setReplyMarkup(markupInline);

        SendMessage message = new SendMessage(); // Create a message object object
        message.setChatId(chatId.toString());
        message.setText("You send /start");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.addAll(Arrays.asList(createReportText));

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.addAll(Arrays.asList(removeReportText, updateReportText));

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        replyKeyboardMarkup.setKeyboard(keyboard);

        sendBotMessageService.sendMessage(message);

        //sendBotMessageService.sendMessage(chatId, START_MESSAGE);
    }

    @Override
    public void handle(Update update, String username) {
        String userMessage = switch (update.getMessage().getText()) {
            case createReportText -> "окей, давай создадим отчет";
            case updateReportText -> "окей, давай обновим отчет";
            case removeReportText -> "окей, давай удалим отчет";
            default -> "хз что ты имеешь в виду";
        };
        SendMessage message = new SendMessage(); // Create a message object object
        message.setChatId(CommandUtils.getChatId(update).toString());
        message.setText(userMessage);
        sendBotMessageService.sendMessage(message);
    }
}
