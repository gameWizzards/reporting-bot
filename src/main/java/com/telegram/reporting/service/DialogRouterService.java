package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface DialogRouterService {
    void handleTelegramUpdateEvent(Update update);

    void startFlow(User user);
}
