package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.I18nPropsResolver;
import com.telegram.reporting.service.RuntimeDialogManager;
import com.telegram.reporting.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final TelegramUserService userService;

    @Override
    public void addPrincipalUser(long chatId, User user) {
        principalUsers.put(chatId, user);
    }

    @Override
    public void removePrincipalUser(long chatId) {
        principalUsers.remove(chatId);
    }

    @Override
    public boolean containsPrincipalUser(long chatId) {
        return principalUsers.containsKey(chatId);
    }

    @Override
    public User getPrincipalUser(long chatId) {
        return principalUsers.get(chatId);
    }

    @Override
    public Locale getPrincipalUserLocale(long chatId) {
        User user = principalUsers.get(chatId);
        if (Objects.isNull(user)) {
            //if authorized user in dialog and app was restarted
            user = userService.findByChatId(chatId);
            principalUsers.put(chatId, user);
        }
        return Objects.nonNull(user) ? principalUsers.get(chatId).getLocale() : I18nPropsResolver.DEFAULT_LOCALE;
    }

    // TODO use this to check access to dialogs in case when role was deleted but dialogs buttons remained in telegram chat story
    @Override
    public Set<Role> getPrincipalUserRoles(long chatId) {
        return principalUsers.get(chatId).getRoles();
    }
}
