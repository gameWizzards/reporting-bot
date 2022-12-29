package com.telegram.reporting.dialogs;

public interface StateMachineHandler {
    void handleButtonClick(Long chatId, ButtonLabelKey buttonLabelKey);

    void handleUserInput(Long chatId, String userInput);

    StateMachineHandler initStateMachine(Long chatId);

    void removeDialogData(Long chatId);
}
