package com.telegram.reporting.dialogs;

import com.telegram.reporting.messages.Message;
import org.telegram.telegrambots.meta.api.objects.Contact;

public interface StateMachineHandler {

    void handleMessage(Long chatId, Message message);

    void handleUserInput(Long chatId, String userInput);

    StateMachineHandler initStateMachine(Long chatId, String telegramNickname);
}
