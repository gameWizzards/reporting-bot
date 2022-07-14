package com.telegram.reporting.utils;

import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.repository.entity.User;
import org.apache.commons.lang3.Validate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MessageConvertorUtils {

    private MessageConvertorUtils() {
    }

    public static String convertToTimeRecordMessage(TimeRecordTO timeRecordTO) {
        Validate.notNull(timeRecordTO, "Required not null TimeRecordTO object to create message");
        return """
                Категория рабочего времени - "%s".
                Затраченное время - %s ч.
                Примечание - "%s."
                 """.formatted(timeRecordTO.getCategoryName(), timeRecordTO.getHours(), timeRecordTO.getNote());
    }

    public static String convertToListTimeRecordsMessage(List<TimeRecordTO> timeRecordTOS) {
        Validate.notEmpty(timeRecordTOS, "Can't covert TimeRecordsTO to message. List of TimeRecordTOs is empty or NULL");

        Long ordinalNumber = 1L;
        StringBuilder timeRecordsMessage = new StringBuilder();
        for (TimeRecordTO timeRecordTO : timeRecordTOS) {
            timeRecordTO.setOrdinalNumber(ordinalNumber);
            String trMessage = convertToTimeRecordMessage(timeRecordTO);
            timeRecordsMessage.append(ordinalNumber)
                    .append(". ")
                    .append(trMessage)
                    .append("\n");
            ordinalNumber++;
        }
        return timeRecordsMessage.toString();
    }

    public static String convertToStatisticMessage(Report report) {
        if (report == null || CollectionUtils.isEmpty(report.getTimeRecords())) {
            return "";
        }
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

    public static String convertToListUsersMessage(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return "";
        }

        StringBuilder message = new StringBuilder();
        for (User user : users) {
            String roles = user.getRoles().stream()
                    .map(r -> r.name().replaceAll("_ROLE", ""))
                    .collect(Collectors.joining(", "));
            message.append("""
                            ChatId - <pre>%d</pre>
                            Name - %s
                            Roles - %s
                            Phone - +%s
                            """.formatted(user.getChatId(), user.getName(), roles, user.getPhone()))
                    .append("\n");
        }
        return message.toString();
    }
}
