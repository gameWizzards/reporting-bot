package com.telegram.reporting.dialogs;

public interface StateMachineHandler {

    void handleMessage(Long chatId, ButtonValue buttonValue);

    void handleUserInput(Long chatId, String userInput);

    StateMachineHandler initStateMachine(Long chatId, String telegramNickname);
}
