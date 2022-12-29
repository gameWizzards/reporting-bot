package com.telegram.reporting.service;

import com.telegram.reporting.dialogs.I18nKey;

import java.util.Locale;

public interface I18nPropsResolver {
    Locale DEFAULT_LOCALE = Locale.forLanguageTag("uk-UA");
    Locale UA_LOCALE = Locale.forLanguageTag("uk-UA");
    Locale RU_LOCALE = Locale.forLanguageTag("ru-RU");

    String getPropsValue(Long chatId, String key);

    String getPropsValue(Long chatId, I18nKey key);

    String getPropsValue(Long chatId, String key, String... args);
}