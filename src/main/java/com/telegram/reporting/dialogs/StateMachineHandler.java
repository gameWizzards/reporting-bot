package com.telegram.reporting.dialogs;

import com.telegram.reporting.messages.MessageEvent;

public interface StateMachineHandler {

    void handleMessageEvent(MessageEvent messageEvent);

    void handleUserInput(String userInput);
}
