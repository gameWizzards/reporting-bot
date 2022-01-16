package com.telegram.reporting.dialogs;

import com.telegram.reporting.messages.Message;

public interface StateMachineHandler {

    void handleMessage(Message message);

    void handleUserInput(String userInput);
}
