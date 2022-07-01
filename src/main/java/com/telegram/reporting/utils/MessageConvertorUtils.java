package com.telegram.reporting.utils;

import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class MessageConvertorUtils {

    private MessageConvertorUtils() {
    }

    public static String convertToMessage(TimeRecordTO timeRecordTO) {
        Validate.notNull(timeRecordTO, "Required not null TimeRecordTO object to create message");
        return """
                Категория рабочего времени - "%s".
                Затраченное время - %s ч.
                Примечание - "%s."
                 """.formatted(timeRecordTO.getCategoryName(), timeRecordTO.getHours(), timeRecordTO.getNote());
    }

    public static String convertToMessage(List<TimeRecordTO> timeRecordTOS) {
        Validate.notEmpty(timeRecordTOS, "Can't covert TimeRecordsTO to message. List of TimeRecordTOs is empty or NULL");

        Long ordinalNumber = 1L;
        StringBuilder timeRecordsMessage = new StringBuilder();
        for (TimeRecordTO timeRecordTO : timeRecordTOS) {
            timeRecordTO.setOrdinalNumber(ordinalNumber);
            String trMessage = convertToMessage(timeRecordTO);
            timeRecordsMessage.append(ordinalNumber)
                    .append(". ")
                    .append(trMessage)
                    .append("\n");
            ordinalNumber++;
        }
        return timeRecordsMessage.toString();
    }

    public static String convertToStatisticMessage(Report report) {
        StringBuilder statisticMessage = new StringBuilder();
        long totalHours = 0;
        String date = DateTimeUtils.toDefaultFormat(report.getDate());

        for (TimeRecord tr : report.getTimeRecords()) {
            String category = tr.getCategory().getName();
            totalHours += tr.getHours();

            statisticMessage
                    .append("*   \"%s\" - %d ч.".formatted(category, tr.getHours()))
                    .append("\n");
        }
        return """
                %s - %d ч.
                %s
                 """.formatted(date, totalHours, statisticMessage);
    }

}
