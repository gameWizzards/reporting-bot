package com.telegram.reporting.service;

import com.telegram.reporting.domain.Role;
import com.telegram.reporting.domain.User;

import java.util.Locale;
import java.util.Set;

public interface RuntimeDialogManager {

    User addPrincipalUser(Long chatId);

    void removePrincipalUser(Long chatId);

    boolean containsPrincipalUser(Long chatId);

    User getPrincipalUser(Long chatId);

    Locale getPrincipalUserLocale(Long chatId);

    Set<Role> getPrincipalUserRoles(Long chatId);
}
