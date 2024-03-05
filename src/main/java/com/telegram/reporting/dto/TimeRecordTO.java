package com.telegram.reporting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeRecordTO implements Ordinal {

    private Long id;
    private Long ordinalNumber;
    private Long reportId;
    private Integer hours;
    private String note;
    private String categoryNameKey;

    @Override
    public Long getOrdinal() {
        return getOrdinalNumber();
    }
}
