package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.UserRepository;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.TelegramUserService;
import org.apache.commons.lang3.StringUtils;
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

    private final UserRepository userRepository;

    public TelegramUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return Optional.ofNullable(userRepository.findByChatId(chatId));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User verifyContact(Message message) {
        if (message == null || message.getContact() == null) {
            return null;
        }
        Contact contact = message.getContact();

        User user = userRepository.findByPhone(contact.getPhoneNumber().replaceAll(" ", ""));
        if (user == null) {
            return null;
        }

        return updateUser(user, contact, message.getChatId());
    }

    private User updateUser(User user, Contact contact, Long chatId) {
        user.setChatId(chatId);
        if (StringUtils.isBlank(user.getName())) {
            user.setName(contact.getFirstName());
        }
        if (StringUtils.isBlank(user.getSurname()) && StringUtils.isNotBlank(contact.getLastName())) {
            user.setSurname(contact.getLastName());
        } else {
            // need for better identification in report or list of employee
            user.setSurname(contact.getPhoneNumber());
        }
        return save(user);
    }
}
