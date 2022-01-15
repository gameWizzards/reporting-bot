package com.telegram.reporting.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface DialogRouterService {
    void handleTelegramUpdateEvent(Update update);
}
