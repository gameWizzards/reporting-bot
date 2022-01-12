package com.telegram.reporting.messages;

import org.apache.commons.lang3.NotImplementedException;

public non-sealed class CreateReportMessageHandler implements MessageHandler {

    @Override
    public void handle() {
        throw new NotImplementedException("not implemented yet");
    }

    @Override
    public String text() {
        return "Добавить отчет";
    }
}
