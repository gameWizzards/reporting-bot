package com.telegram.reporting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeTariffMessageTO implements Ordinal {

    private Long id;
    private Long chatId;
    private String fullName;
    private String tariffPerHour;
    private String tariffPerDay;
    private String averageComputedSalary;

    private Long ordinalNumber;

    public EmployeeTariffMessageTO() {
    }

    @Override
    public Long getOrdinal() {
        return ordinalNumber;
    }
}
