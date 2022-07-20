package com.telegram.reporting.service.impl;

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
import java.util.*;

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
    public List<User> findUsers(UserFilter filter) {
        Validate.notEmpty(filter.userStatus(), "Required to use UserStatus in UserFilter. UserStatus is empty or NULL");
        Validate.noNullElements(filter.userStatus(), "UserStatus in UserFilter contains NULL element");

        List<User> result = new ArrayList<>();
        for (UserFilter.UserStatus status : filter.userStatus()) {
            switch (status) {
                case ACTIVE -> result.addAll(userRepository.findUsers(filter.name(), filter.surname(), false));
                case DELETED -> result.addAll(userRepository.findUsers(filter.name(), filter.surname(), true));
                case NOT_VERIFIED -> result.addAll(userRepository.findAllNotVerified());
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

        return updateUser(user, contact, message.getChatId(), message.getFrom().getUserName());
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

    private User updateUser(User user, Contact contact, Long chatId, String telegramNickName) {
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
        return save(user);
    }
}
