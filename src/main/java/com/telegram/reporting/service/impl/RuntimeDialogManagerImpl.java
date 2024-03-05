package com.telegram.reporting.service.impl;

import com.telegram.reporting.exception.TelegramUserDeletedException;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.domain.Role;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.service.RuntimeDialogManager;
import com.telegram.reporting.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuntimeDialogManagerImpl implements RuntimeDialogManager {
    private final Map<Long, User> principalUsers = new ConcurrentHashMap<>();

    private final UserService userService;

    @Override
    public User addPrincipalUser(Long chatId) {
        Validate.notNull(chatId, "ChatId is required to add user principal");
        User user = userService.findByChatId(chatId);
        if (Objects.isNull(user)) {
            throw new TelegramUserException("Can't set user as principal. Reason: User is not exist with chatId: " + chatId);
        }

        if (user.isDeleted()) {
            throw new TelegramUserDeletedException(
                    chatId,
                    "Can't set user as principal. Reason: User was deleted. User: %s, ChatId: %d".formatted(user.getFullName(), chatId));
        }
        principalUsers.put(chatId, user);
        return user;
    }

    @Override
    public void removePrincipalUser(Long chatId) {
        principalUsers.remove(chatId);
    }

    @Override
    public boolean containsPrincipalUser(Long chatId) {
        return principalUsers.containsKey(chatId);
    }

    @Override
    public User getPrincipalUser(Long chatId) {
        Validate.notNull(chatId, "ChatId is required to get user principal");
        if (!principalUsers.containsKey(chatId)) {
            return addPrincipalUser(chatId);
        }
        return principalUsers.get(chatId);
    }

    @Override
    public Locale getPrincipalUserLocale(Long chatId) {
        return getPrincipalUser(chatId).getLocale();
    }

    @Override
    public Set<Role> getPrincipalUserRoles(Long chatId) {
        return getPrincipalUser(chatId).getRoles();
    }
}
