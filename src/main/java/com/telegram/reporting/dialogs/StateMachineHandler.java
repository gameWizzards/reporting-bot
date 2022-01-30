package com.telegram.reporting.dialogs;

import com.telegram.reporting.messages.Message;

public interface StateMachineHandler {

    void handleMessage(Long chatId, Message message);

    void handleUserInput(Long chatId, String userInput);

    StateMachineHandler initStateMachine(Long chatId);
}
