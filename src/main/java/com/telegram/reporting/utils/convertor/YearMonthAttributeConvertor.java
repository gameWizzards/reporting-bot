package com.telegram.reporting.utils.convertor;

import javax.persistence.AttributeConverter;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class YearMonthAttributeConvertor implements AttributeConverter<YearMonth, String> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("L-yyyy", Locale.ENGLISH);

    @Override
    public String convertToDatabaseColumn(YearMonth attribute) {
        if (attribute != null) {
            return attribute.format(formatter);
        }
        return null;
    }

    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        if (dbData != null) {

            return YearMonth.parse(dbData, formatter);
        }
        return null;
    }

}
