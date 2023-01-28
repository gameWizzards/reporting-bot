package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.repository.entity.User;

import java.util.Locale;
import java.util.Set;

public interface RuntimeDialogManager {

    void addPrincipalUser(Long chatId, User user);

    void removePrincipalUser(Long chatId);

    boolean containsPrincipalUser(Long chatId);

    User getPrincipalUser(Long chatId);

    Locale getPrincipalUserLocale(Long chatId);

    Set<Role> getPrincipalUserRoles(Long chatId);
}
