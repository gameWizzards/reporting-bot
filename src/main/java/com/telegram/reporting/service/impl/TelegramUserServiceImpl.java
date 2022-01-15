package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.TelegramUserRepository;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.TelegramUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TelegramUserService}.
 */
@Service
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    @Autowired
    public TelegramUserServiceImpl(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    @Override
    public void save(User user) {
        telegramUserRepository.save(user);
    }

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return telegramUserRepository.findById(chatId);
    }

    @Override
    public List<User> findAll() {
        return telegramUserRepository.findAll();
    }
}
