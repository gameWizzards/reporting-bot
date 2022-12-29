package com.telegram.reporting.utils;

import com.telegram.reporting.service.I18nPropsResolver;

import javax.persistence.AttributeConverter;
import java.util.Locale;
import java.util.Objects;

public class LocaleConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale locale) {
        return Objects.nonNull(locale)
                ? locale.toLanguageTag()
                : I18nPropsResolver.DEFAULT_LOCALE.toLanguageTag();
    }

    @Override
    public Locale convertToEntityAttribute(String languageTag) {
        if (Objects.nonNull(languageTag) && !languageTag.isEmpty()) {
            return Locale.forLanguageTag(languageTag);
        }
        return null;
    }
}
