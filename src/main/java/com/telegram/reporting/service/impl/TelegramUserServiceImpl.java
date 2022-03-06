package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.TelegramUserRepository;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.TelegramUserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TelegramUserService}.
 */
@Service
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

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

    @Override
    public User verifyContact(Message message) {
        if (message == null || message.getContact() == null) {
            return null;
        }
        Contact contact = message.getContact();

        User user = telegramUserRepository.findByPhone(contact.getPhoneNumber());
        if (user == null) {
            return null;
        }

        user.setChatId(message.getChatId());
        user.setName(contact.getFirstName());
        user.setSurname(contact.getLastName());

        return telegramUserRepository.save(user);
    }
}
