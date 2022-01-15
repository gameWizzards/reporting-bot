package com.telegram.reporting.dialogs.create_report;

import com.telegram.reporting.messages.MessageEvent;

public interface StateMachineHandler {

    void handleMessageEvent();

    void handleUserInput(String userInput);
}
