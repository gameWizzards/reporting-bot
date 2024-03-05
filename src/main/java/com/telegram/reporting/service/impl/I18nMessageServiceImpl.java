package com.telegram.reporting.service.impl;

import com.telegram.reporting.domain.*;
import com.telegram.reporting.dto.EmployeeTO;
import com.telegram.reporting.dto.TimeRecordTO;
import com.telegram.reporting.exception.MismatchCategoryException;
import com.telegram.reporting.i18n.ButtonLabelKey;
import com.telegram.reporting.i18n.I18nKey;
import com.telegram.reporting.i18n.MessageKey;
import com.telegram.reporting.i18n.MonthKey;
import com.telegram.reporting.repository.EmployeeTariffRepository;
import com.telegram.reporting.service.CacheService;
import com.telegram.reporting.service.CategoryService;
import com.telegram.reporting.service.I18nMessageService;
import com.telegram.reporting.service.I18nPropsResolver;
import com.telegram.reporting.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class I18nMessageServiceImpl implements I18nMessageService {
    private final CategoryService categoryService;
    private final I18nPropsResolver i18nPropsResolver;
    private final EmployeeTariffRepository employeeTariffRepository;

    @Override
    public String getMessage(Long chatId, String key) {
        return i18nPropsResolver.getPropsValue(chatId, key);
    }

    @Override
    public String getMessage(Long chatId, I18nKey key) {
        return i18nPropsResolver.getPropsValue(chatId, key.value());
    }

    @Override
    public String getMessage(Long chatId, I18nKey key, String... args) {
        return i18nPropsResolver.getPropsValue(chatId, key.value(), args);
    }

    @Override
    public String convertToTimeRecordMessage(Long chatId, TimeRecordTO timeRecordTO) {
        Validate.notNull(timeRecordTO, "Required not null TimeRecordTO object to create message");
        ButtonLabelKey categoryKey = ButtonLabelKey.getByKey(timeRecordTO.getCategoryNameKey());

        String category = Objects.nonNull(categoryKey) && categoryService.isCategory(categoryKey.value())
                ? getMessage(chatId, categoryKey)
                : timeRecordTO.getCategoryNameKey();

        return getMessage(chatId, MessageKey.COMMON_BASE_TIME_RECORD_MESSAGE,
                category,
                timeRecordTO.getHours().toString(),
                timeRecordTO.getNote());
    }

    @Override
    public String convertToListTimeRecordsMessage(Long chatId, List<TimeRecordTO> timeRecordTOS) {
        if (CollectionUtils.isEmpty(timeRecordTOS)) {
            return "";
        }
        Long ordinalNumber = 1L;
        StringBuilder timeRecordsMessage = new StringBuilder();
        for (TimeRecordTO timeRecordTO : timeRecordTOS) {
            timeRecordTO.setOrdinalNumber(ordinalNumber);
            String trMessage = convertToTimeRecordMessage(chatId, timeRecordTO);
            timeRecordsMessage.append(ordinalNumber)
                    .append(". ")
                    .append(trMessage)
                    .append("\n\n");
            ordinalNumber++;
        }
        return timeRecordsMessage.toString();
    }

    @Override
    public String convertToStatisticMessage(Long chatId, Report report) {
        if (Objects.isNull(report) || CollectionUtils.isEmpty(report.getTimeRecords())) {
            return "";
        }
        StringBuilder statisticSubMessage = new StringBuilder();
        Integer totalHours = 0;
        String date = DateTimeUtils.toDefaultFormat(report.getDate());

        for (TimeRecord tr : report.getTimeRecords()) {
            String category = getMessage(chatId, tr.getCategory().getNameKey());
            String note = tr.getNote().contains("NA") ? "" : " \uD83D\uDCAC%s".formatted(tr.getNote());
            totalHours += tr.getHours();

            statisticSubMessage.append(getMessage(chatId,
                    MessageKey.GS_BASE_STATISTIC_SUB_MESSAGE,
                    category,
                    tr.getHours().toString(),
                    note));
        }

        return getMessage(chatId, MessageKey.GS_BASE_STATISTIC_MESSAGE,
                date,
                totalHours.toString(),
                statisticSubMessage.toString());

    }

    @Override
    public String convertToListUsersMessage(Long chatId, List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return getMessage(chatId, MessageKey.ALU_EMPTY_USER_LIST);
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

    @Override
    public String prepareHoursByCategoryMessage(Long chatId, Map<String, Integer> categoryHours) {
        String empty = getMessage(chatId, MessageKey.GS_ABSENT_REPORTS);

        StringBuilder message = new StringBuilder(getMessage(chatId, MessageKey.GS_BY_CATEGORIES));

        categoryHours.forEach((key, value) -> message.append(
                getMessage(chatId, MessageKey.GS_HOURS_BY_CATEGORY_MESSAGE,
                        key, value.toString())));

        return message.isEmpty() ? empty : message.toString();
    }

    @Override
    public String convertToListEmployeeMessage(Long chatId, List<EmployeeTO> employeeTOS) {
        if (CollectionUtils.isEmpty(employeeTOS)) {
            return "";
        }
        Long ordinalNumber = 1L;
        StringBuilder listEmployeeMessage = new StringBuilder();
        for (EmployeeTO employeeTO : employeeTOS) {
            String statusEmployee = employeeTO.isDeleted()
                    ? getMessage(chatId, MessageKey.COMMON_EMPLOYEE_STATUS_DELETED)
                    : getMessage(chatId, MessageKey.COMMON_EMPLOYEE_STATUS_ACTIVATED);

            String fullName = employeeTO.isActivated()
                    ? employeeTO.getFullName()
                    : getMessage(chatId, MessageKey.COMMON_USER_NOT_AUTHORIZED);

            employeeTO.setOrdinalNumber(ordinalNumber);

            listEmployeeMessage.append(ordinalNumber)
                    .append(". ")
                    .append(getMessage(chatId, MessageKey.MESTATUS_LIST_EMPLOYEES_STATUS,
                            fullName, employeeTO.getPhone(), statusEmployee));
            ordinalNumber++;
        }
        return listEmployeeMessage.toString();
    }

    @Override
    public String convert2EmployeeStatusInfoMessage(Long chatId, EmployeeTO employeeTO) {
        String fullName = employeeTO.isActivated()
                ? employeeTO.getFullName()
                : getMessage(chatId, MessageKey.COMMON_USER_NOT_AUTHORIZED);

        String roles = employeeTO.getRoles().stream()
                .map(role -> convertRole2Text(chatId, role))
                .collect(Collectors.joining(", "));

        String employeeStatus = employeeTO.isDeleted()
                ? getMessage(chatId, MessageKey.COMMON_EMPLOYEE_STATUS_DELETED)
                : getMessage(chatId, MessageKey.COMMON_EMPLOYEE_STATUS_ACTIVATED);

        return getMessage(chatId, MessageKey.MESTATUS_EMPLOYEE_INFO_STATUS_MESSAGE,
                fullName, employeeTO.getPhone(), roles, employeeStatus);
    }

    @Cacheable(value = CacheService.EMPLOYEE_STATISTIC_CACHE,
            key = "#chatId.toString + #statisticDate.monthValue + #statisticDate.year")
    @Override
    public String createMonthStatisticMessage(Long chatId, LocalDate statisticDate, List<Report> reports) {
        AtomicInteger ordinalNumb = new AtomicInteger(1);
        Map<String, Integer> categoryHours = new HashMap<>();

        reports.parallelStream()
                .map(Report::getTimeRecords)
                .flatMap(Collection::stream)
                .forEach(tr -> {
                    String localizedCategoryLabel = getMessage(chatId, tr.getCategory().getNameKey());
                    Integer hours = categoryHours.getOrDefault(localizedCategoryLabel, 0);
                    categoryHours.put(localizedCategoryLabel, hours + tr.getHours());
                });

        int sumHours = categoryHours.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        String categoryHoursMessage = prepareHoursByCategoryMessage(chatId, categoryHours);

        String statistic = reports.stream()
                .map(report -> convertToStatisticMessage(chatId, report))
                .map(s -> ordinalNumb.getAndIncrement() + ". " + s)
                .collect(Collectors.joining());

        String localizedMonth = getMessage(chatId, MonthKey.getMonthByOrdinal(statisticDate.getMonthValue()));

        return getMessage(chatId, MessageKey.COMMON_TOTAL_MONTH_STATISTIC_MESSAGE,
                localizedMonth,
                Integer.toString(sumHours),
                categoryHoursMessage,
                statistic);
    }

    @Override
    public String convertToCompanyTariffsMessage(Long chatId, List<Tariff> tariffs) {
        String companyTariffsMessage = tariffs.stream()
                .map(tariff -> convertToTariffRow(chatId, tariff))
                .collect(Collectors.joining("\n"));

        return getMessage(chatId, MessageKey.TLT_COMPANY_TARIFFS,
                companyTariffsMessage);
    }

    @Override
    public String convertToOverriddenTariffsByCategoryMessage(Long chatId, ButtonLabelKey tariffCategory) {

        var overriddenTariffs = employeeTariffRepository.getOverriddenTariffsByCategory(tariffCategory.value());
        String overriddenTariffsMessage =  overriddenTariffs.stream()
                .map(employeeTariff -> convertToOverriddenTariffRow(chatId, employeeTariff))
                .collect(Collectors.joining("\n"));

        var overriddenTariff = overriddenTariffs.stream().findFirst()
                .orElseThrow(() -> new MismatchCategoryException("No overridden tariffs for category: " + tariffCategory.value()));

        var tariffName = getMessage(chatId, overriddenTariff.getTariff().getCategory().getNameKey());
        var companyTariffPerHour = overriddenTariff.getTariff().getTariffication();

        return getMessage(chatId, MessageKey.TLT_OVERRIDDEN_TARIFFS_BY_CATEGORY,
                tariffName,
                companyTariffPerHour.toPlainString(),
                overriddenTariffsMessage);
    }
//       Список сотрудников с индивидуальными тарифами.
//       Тариф - "{categoryName}" - {tariffication} грн/час.

//       *   {fullname} - {tariffication} грн/час.
//       выбрать другую категорию? + кнопка "выбрать категорию"

    private String convertToOverriddenTariffRow(Long chatId, EmployeeTariff employeeTariff) {


        var employeeFullName = employeeTariff.getEmployee().getFullName();
        var employeeTariffPerHour = employeeTariff.getOverriddenTariffication();

        var roundUp = new MathContext(2, RoundingMode.HALF_UP);
        var dayTariff = employeeTariffPerHour.multiply(BigDecimal.valueOf(8), roundUp);
        var monthAverageTariff = DateTimeUtils.getAverageWorkingDaysPerMonth().multiply(dayTariff, roundUp);

        return getMessage(chatId, MessageKey.TLT_TARIFF_ROW,
                employeeFullName,
                employeeTariffPerHour.toPlainString(),
                dayTariff.toPlainString(),
                monthAverageTariff.toPlainString());
    }

    // TODO: consider to move the common logic to separate method for all tariff row converters
    private String convertToTariffRow(Long chatId, Tariff tariff) {
        var tariffPerHour = tariff.getTariffication();

        var roundUp = new MathContext(2, RoundingMode.HALF_UP);
        var dayTariff = tariffPerHour.multiply(BigDecimal.valueOf(8), roundUp);
        var monthAverageTariff = DateTimeUtils.getAverageWorkingDaysPerMonth().multiply(dayTariff, roundUp);

        var tariffName = getMessage(chatId, tariff.getCategory().getNameKey());

        return getMessage(chatId, MessageKey.TLT_TARIFF_ROW,
                tariffName,
                tariffPerHour.toPlainString(),
                dayTariff.toPlainString(),
                monthAverageTariff.toPlainString());
    }

    private String convertRole2Text(Long chatId, Role role) {
        return switch (role) {
            case EMPLOYEE_ROLE -> i18nPropsResolver.getPropsValue(chatId, MessageKey.COMMON_EMPLOYEE_ROLE);
            case MANAGER_ROLE -> i18nPropsResolver.getPropsValue(chatId, MessageKey.COMMON_MANAGER_ROLE);
            case TARIFF_EDITOR_ROLE -> i18nPropsResolver.getPropsValue(chatId, MessageKey.COMMON_TARIFF_EDITOR_ROLE);
            case ADMIN_ROLE -> i18nPropsResolver.getPropsValue(chatId, MessageKey.COMMON_ADMIN_ROLE);
            default -> i18nPropsResolver.getPropsValue(chatId, MessageKey.COMMON_WARNING_ROLE_UNMAPPED);
        };
    }

}
