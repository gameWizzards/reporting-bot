package com.telegram.reporting.service;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

public interface DialogRouterService {
    void handleTelegramUpdateEvent(Update update);

    void startFlow(Long chatId, Locale locale);
}
