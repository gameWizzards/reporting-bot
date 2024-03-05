package com.telegram.reporting.mapper;

import com.telegram.reporting.domain.EmployeeTariff;
import com.telegram.reporting.dto.EmployeeTariffMessageTO;
import com.telegram.reporting.utils.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface EmployeeTariffMessageMapper extends BaseMapper<EmployeeTariff, EmployeeTariffMessageTO>{

    @Mapping(target = "id", source = "employee.id")
    @Mapping(target = "chatId", source = "employee.chatId")
    @Mapping(target = "fullName", source = "employee.fullName")
    @Mapping(target = "tariffPerHour", source = "overriddenTariffication", qualifiedByName = "toTariffPerHour")
    @Mapping(target = "tariffPerDay", source = "overriddenTariffication", qualifiedByName = "toTariffPerDay")
    @Mapping(target = "averageComputedSalary", source = "overriddenTariffication", qualifiedByName = "toAverageComputedSalary")
    EmployeeTariffMessageTO toDto(EmployeeTariff employeeTariff);

    @Named("toTariffPerHour")
    default String toTariffPerHour(BigDecimal overriddenTariffication) {
        return overriddenTariffication.toPlainString();
    }

    @Named("toTariffPerDay")
    default String toTariffPerDay(BigDecimal overriddenTariffication) {
        var workingHours = 8;
        return overriddenTariffication
                .multiply(BigDecimal.valueOf(workingHours), new MathContext(2, RoundingMode.HALF_UP))
                .toPlainString();
    }

    @Named("toAverageComputedSalary")
    default String toAverageComputedSalary(BigDecimal overriddenTariffication) {
        var workingHours = 8;
        var roundUp = new MathContext(2, RoundingMode.HALF_UP);
        var dayTariff = overriddenTariffication.multiply(BigDecimal.valueOf(workingHours), roundUp);

        return DateTimeUtils.getAverageWorkingDaysPerMonth()
                .multiply(dayTariff, roundUp)
                .toPlainString();
    }
}