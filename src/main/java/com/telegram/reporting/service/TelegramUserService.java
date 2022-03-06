package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling {@link User} entity.
 */
public interface TelegramUserService {

    /**
     * Save provided {@link User} entity.
     *
     * @param user provided telegram user.
     */
    void save(User user);

    /**
     * Find {@link User} by chatId.
     *
     * @param chatId provided Chat ID
     * @return {@link User} with provided chat ID or null otherwise.
     */
    Optional<User> findByChatId(Long chatId);

    List<User> findAll();

    User verifyContact(Message message);
}
