package com.telegram.reporting.repository.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.telegram.reporting.utils.DateTimeUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeRecordTO {

    private Long id;
    private Integer hours;
    private String note;
    private String categoryName;

    @JsonFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime created;

}
