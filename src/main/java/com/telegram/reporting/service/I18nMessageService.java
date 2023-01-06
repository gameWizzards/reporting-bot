package com.telegram.reporting.service;

import com.telegram.reporting.i18n.I18nKey;
import com.telegram.reporting.repository.dto.EmployeeTO;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface I18nMessageService {

    String getMessage(Long chatId, String key);

    String getMessage(Long chatId, I18nKey key);

    String getMessage(Long chatId, I18nKey key, String... args);

    String convertToTimeRecordMessage(Long chatId, TimeRecordTO timeRecordTO);

    String convertToListTimeRecordsMessage(Long chatId, List<TimeRecordTO> timeRecordTOS);

    String convertToStatisticMessage(Long chatId, Report report);

    String convertToListUsersMessage(Long chatId, List<User> users);

    String prepareHoursByCategoryMessage(Long chatId, Map<String, Integer> categoryHours);

    String convertToListEmployeeMessage(Long chatId, List<EmployeeTO> employeeTOS);

    String convert2EmployeeStatusInfoMessage(Long chatId, EmployeeTO employeeTO);

    String createMonthStatisticMessage(Long chatId, LocalDate statisticDate, List<Report> reports);

}