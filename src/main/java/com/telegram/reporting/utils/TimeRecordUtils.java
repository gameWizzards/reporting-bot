package com.telegram.reporting.utils;

import com.telegram.reporting.repository.dto.TimeRecordTO;
import org.apache.commons.lang3.Validate;

public class TimeRecordUtils {

    public static String convertTimeRecordToMessage(TimeRecordTO timeRecordTO) {
        Validate.notNull(timeRecordTO, "Required not null TimeRecordTO object to create message");
        var timeRecordMessage = """
                Категория рабочего времени - "%s".
                Затраченное время - %s ч.
                Примечание - "%s."
                 """;
        return String.format(timeRecordMessage, timeRecordTO.getCategoryName(), timeRecordTO.getHours(), timeRecordTO.getNote());
    }
}
