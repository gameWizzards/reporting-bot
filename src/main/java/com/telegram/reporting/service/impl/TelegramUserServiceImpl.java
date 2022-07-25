package com.telegram.reporting.service.impl;

import com.telegram.reporting.exception.PhoneFormatException;
import com.telegram.reporting.repository.UserRepository;
import com.telegram.reporting.repository.dto.EmployeeTO;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.TelegramUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TelegramUserServiceImpl implements TelegramUserService {

    private final UserRepository userRepository;

    public TelegramUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        Validate.notNull(user, "User is required to save him)");
        if (user.getCreated() == null) {
            user.setCreated(LocalDateTime.now());
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        Validate.notNull(id, "Id is required to find user by it");
        return userRepository.findById(id);
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
    public List<User> findUsers(UserFilter filter) {
        Validate.notEmpty(filter.userStatus(), "Required to use UserStatus in UserFilter. UserStatus is empty or NULL");
        Validate.noNullElements(filter.userStatus(), "UserStatus in UserFilter contains NULL element");

        List<User> result = new ArrayList<>();
        for (UserFilter.UserStatus status : filter.userStatus()) {
            switch (status) {
                case ACTIVE -> result.addAll(userRepository.findActiveUsers(filter.name(), filter.surname()));
                case DELETED -> result.addAll(userRepository.findDeletedUsers(filter.name(), filter.surname()));
                case ACTIVE_NOT_VERIFIED -> result.addAll(userRepository.findActiveNotVerifiedUsers());
            }
        }

        if (!CollectionUtils.isEmpty(filter.roles())) {
            return result.stream()
                    .filter(res -> !Collections.disjoint(res.getRoles(), filter.roles()))
                    .toList();
        }

        return result;
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

        return activateUser(user, contact, message.getChatId(), message.getFrom().getUserName());
    }

    @Override
    public List<EmployeeTO> findEmployeesWithExistReportsByMonth(LocalDate statisticMonth) {
        Validate.notNull(statisticMonth, "StatisticMonth arg is required");
        List<User> users = userRepository.findUsersWithExistReportsByMonth(
                statisticMonth.getMonthValue(),
                statisticMonth.getYear());
        return users.stream()
                .map(EmployeeTO::new)
                .toList();
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        Validate.notBlank(phone, "Phone is required to search by it");
        String fullFormatPhoneRegex = "^380[0-9]{9}";
        if (!phone.matches(fullFormatPhoneRegex)) {
            throw new PhoneFormatException("Wrong phone number format. Allowed 380971112233. Input=%s".formatted(phone));
        }
        return Optional.ofNullable(userRepository.findByPhone(phone));
    }

    private User activateUser(User user, Contact contact, Long chatId, String telegramNickName) {
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
        user.setTelegramNickname(telegramNickName);
        user.setRoles(Set.of(Role.EMPLOYEE_ROLE));
        user.setActivated(LocalDateTime.now());
        return save(user);
    }
}
