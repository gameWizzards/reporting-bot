package com.telegram.reporting.dialogs;

import com.telegram.reporting.messages.Message;

public abstract class StateMachineHandler {

    protected Long chatId;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public abstract void handleMessage(Message message);

    public abstract void handleUserInput(String userInput);
}
