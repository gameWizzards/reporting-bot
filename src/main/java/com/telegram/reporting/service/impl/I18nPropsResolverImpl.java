package com.telegram.reporting.service.impl;

import com.telegram.reporting.exception.TelegramUserDeletedException;
import com.telegram.reporting.exception.TelegramUserException;
import com.telegram.reporting.i18n.I18nKey;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.service.I18nPropsResolver;
import com.telegram.reporting.service.RuntimeDialogManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class I18nPropsResolverImpl implements I18nPropsResolver {
    private final MessageSource messageSource;
    private final RuntimeDialogManager runtimeDialogManager;

    @Override
    public String getPropsValue(Long chatId, String key) {
        return getPropsValue(chatId, key, null);
    }

    @Override
    public String getPropsValue(Long chatId, I18nKey key) {
        return getPropsValue(chatId, key.value(), null);
    }

    @Override
    public String getPropsValue(Long chatId, String key, String... args) {
        Validate.notBlank(key, "Key is required to resolve I18n properties. ChatId: %d".formatted(chatId));

        User principalUser;

        try {
            principalUser = runtimeDialogManager.getPrincipalUser(chatId);
        } catch (TelegramUserException | TelegramUserDeletedException e) {
            return messageSource.getMessage(key, args, DEFAULT_LOCALE);
        }
        // TODO: mock try catch until doesn't implement  tariff dialogs
        try {

            return messageSource.getMessage(key, args, principalUser.getLocale());
        } catch (Exception e) {
        }
        return key;
    }

}
