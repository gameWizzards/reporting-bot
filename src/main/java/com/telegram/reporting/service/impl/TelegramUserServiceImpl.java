package com.telegram.reporting.service.impl;

import com.telegram.reporting.bot.event.SendContactEvent;
import com.telegram.reporting.exception.PhoneFormatException;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.repository.UserRepository;
import com.telegram.reporting.repository.dto.EmployeeTO;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.repository.filter.UserFilter;
import com.telegram.reporting.service.I18nPropsResolver;
import com.telegram.reporting.service.TelegramUserService;
import com.telegram.reporting.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TelegramUserServiceImpl implements TelegramUserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        Validate.notNull(user, "User is required to save him)");
        if (Objects.isNull(user.getCreated())) {
            user.setCreated(LocalDateTime.now());
        }
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        Validate.notNull(id, "UserId is required to find user by it");
        return userRepository.findById(id)
                .orElseThrow(() -> new TelegramUserException("Can't find user with id = %s".formatted(id)));
    }

    @Override
    public User findByChatId(Long chatId) {
        Validate.notNull(chatId, "ChatId is required to find user by it");
        return userRepository.findByChatId(chatId)
                .orElse(null);
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
                case DELETED_NOT_VERIFIED -> result.addAll(userRepository.findDeleteNotVerifiedUsers());
            }
        }

        if (!CollectionUtils.isEmpty(filter.roles())) {
            return result.stream()
                    .filter(user -> containsRoleInFilter(user, filter))
                    .toList();
        }
        return result;
    }

    public User verifyContact(SendContactEvent event) {
        User user = userRepository.findByPhone(event.normalizedPhoneNumber())
                .orElseThrow(() -> new TelegramUserException("User with phone number [%s] is not registered yet! ChatId = %s."
                        .formatted(event.normalizedPhoneNumber(), event.chatId())));

        return activateUser(user, event);
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
    public User findByPhone(String phone) {
        Validate.notBlank(phone, "Phone is required to search by it");

        if (!CommonUtils.isCorrectPhoneFormat(phone)) {
            throw new PhoneFormatException("Wrong phone number format. Allowed 380971112233. Input=%s".formatted(phone));
        }
        return userRepository.findByPhone(phone)
                .orElse(null);
    }

    @Override
    public void removeNotAuthorizedUsers(User user) {
        Validate.notNull(user, "User is required to delete");
        userRepository.delete(user);
    }

    private User activateUser(User user, SendContactEvent event) {
        user.setChatId(event.chatId());
        user.setName(event.name());
        user.setSurname(event.surname());
        user.setTelegramNickname(event.nickName());
        user.setRoles(Set.of(Role.EMPLOYEE_ROLE));
        user.setLocale(I18nPropsResolver.DEFAULT_LOCALE);
        user.setActivated(LocalDateTime.now());
        return save(user);
    }

    private boolean containsRoleInFilter(User user, UserFilter filter) {
        return !Collections.disjoint(user.getRoles(), filter.roles());
    }
}
