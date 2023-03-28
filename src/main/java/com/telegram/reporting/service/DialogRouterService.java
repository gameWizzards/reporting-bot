package com.telegram.reporting.service;

import com.telegram.reporting.bot.event.TelegramEvent;

import java.util.Locale;

public interface DialogRouterService {

    void handleTelegramEvent(TelegramEvent event);

    void startFlow(Long chatId, Locale locale);
}
