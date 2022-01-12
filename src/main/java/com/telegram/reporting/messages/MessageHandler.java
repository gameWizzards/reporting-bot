package com.telegram.reporting.messages;

public sealed interface MessageHandler
        permits CreateReportMessageHandler {

    void handle();

    String text();
}
