package com.miha.telegram.command;

import com.miha.telegram.bot.MihaTelegramBot;
import com.miha.telegram.service.SendBotMessageService;
import com.miha.telegram.service.SendBotMessageServiceImpl;
import com.miha.telegram.service.TelegramUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Abstract class for testing {@link Command}s.
 */
abstract class AbstractCommandTest {

    protected MihaTelegramBot telegramBot = Mockito.mock(MihaTelegramBot.class);
    protected SendBotMessageService sendBotMessageService = new SendBotMessageServiceImpl(telegramBot);
    protected TelegramUserService telegramUserService = Mockito.mock(TelegramUserService.class);

    public static Update prepareUpdate(Long chatId, String commandName) {
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getText()).thenReturn(commandName);
        update.setMessage(message);
        return update;
    }

    abstract String getCommandName();

    abstract String getCommandMessage();

    abstract Command getCommand();

    @Test
    public void shouldProperlyExecuteCommand() throws TelegramApiException {
        //given
        Long chatId = 1234567824356L;

        Update update = prepareUpdate(chatId, getCommandName());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(getCommandMessage());
        sendMessage.enableHtml(true);

        //when
        getCommand().execute(update);

        //then
        Mockito.verify(telegramBot).execute(sendMessage);
    }
}
