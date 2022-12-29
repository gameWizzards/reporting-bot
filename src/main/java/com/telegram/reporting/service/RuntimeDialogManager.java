package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.repository.entity.User;

import java.util.Locale;
import java.util.Set;

public interface RuntimeDialogManager {

    void addPrincipalUser(long chatId, User user);

    void removePrincipalUser(long chatId);

    boolean containsPrincipalUser(long chatId);

    User getPrincipalUser(long chatId);

    Locale getPrincipalUserLocale(long chatId);

    Set<Role> getPrincipalUserRoles(long chatId);
}
