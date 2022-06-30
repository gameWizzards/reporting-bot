package com.telegram.reporting.utils;

import com.telegram.reporting.repository.dto.TimeRecordTO;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class TimeRecordUtils {

    public static String convertTimeRecordToMessage(TimeRecordTO timeRecordTO) {
        Validate.notNull(timeRecordTO, "Required not null TimeRecordTO object to create message");
        return """
                Категория рабочего времени - "%s".
                Затраченное время - %s ч.
                Примечание - "%s."
                 """.formatted(timeRecordTO.getCategoryName(), timeRecordTO.getHours(), timeRecordTO.getNote());
    }

    public static String convertListTimeRecordsToMessage(List<TimeRecordTO> timeRecordTOS) {
        Validate.notEmpty(timeRecordTOS, "Can't covert TimeRecordsTO to message. List of TimeRecordTOs is empty or NULL");

        Long ordinalNumber = 1L;
        StringBuilder timeRecordsMessage = new StringBuilder();
        for (TimeRecordTO timeRecordTO : timeRecordTOS) {
            timeRecordTO.setOrdinalNumber(ordinalNumber);
            String trMessage = convertTimeRecordToMessage(timeRecordTO);
            timeRecordsMessage.append(ordinalNumber)
                    .append(". ")
                    .append(trMessage)
                    .append("\n");
            ordinalNumber++;
        }
        return timeRecordsMessage.toString();
    }
}
