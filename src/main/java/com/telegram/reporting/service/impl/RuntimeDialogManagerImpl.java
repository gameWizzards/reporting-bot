package com.telegram.reporting.service.impl;

import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.repository.entity.User;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.RuntimeDialogManager;
import com.telegram.reporting.service.SendBotMessageService;
import com.telegram.reporting.service.TelegramUserService;
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

    private final TelegramUserService userService;
    private final SendBotMessageService sendBotMessageService;
    private final I18nMessageService i18nMessageService;


    @Override
    public void addPrincipalUser(Long chatId, User user) {
        Validate.notNull(chatId, "ChatId is required to add user principal");
        Validate.notNull(user, "User must not be null to add him ass user principal for chat: [%d]", chatId);
        principalUsers.put(chatId, user);
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
        if (principalUsers.containsKey(chatId)) {
            return principalUsers.get(chatId);
        }

        User user = userService.findByChatId(chatId);
        if (Objects.isNull(user) || user.isDeleted()) {
            String reason = Objects.isNull(user)
                    ? i18nMessageService.getMessage(chatId, MessageKey.PD_ACCOUNT_NOT_FOUND)
                    : i18nMessageService.getMessage(chatId, MessageKey.PD_ACCOUNT_DELETED);

            sendBotMessageService.sendMessage(chatId,
                    i18nMessageService.getMessage(chatId, MessageKey.PD_FAILED_ACCOUNT_CHECKING, reason));

            // if user deleted
            removePrincipalUser(chatId);
            throw new TelegramUserException("User is not available to set his as principal. ChatId: %s. User: %s".formatted(chatId, user));
        }
        addPrincipalUser(chatId, user);
        return user;
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
