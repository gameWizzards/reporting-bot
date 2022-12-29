package com.telegram.reporting.repository.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.utils.DateTimeUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeRecordTO implements Serializable, Ordinal {

    private Long id;
    private Long ordinalNumber;
    private Long reportId;
    private Integer hours;
    private String note;
    private String categoryNameKey;

    @JsonFormat(pattern = DateTimeUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime created;

    public TimeRecordTO() {
    }

    public TimeRecordTO(TimeRecord timeRecord) {
        this.id = timeRecord.getId();
        this.reportId = timeRecord.getReport().getId();
        this.hours = timeRecord.getHours();
        this.note = timeRecord.getNote();
        this.categoryNameKey = timeRecord.getCategory().getNameKey();
        this.created = timeRecord.getCreated();
    }

    @Override
    public Long getOrdinal() {
        return ordinalNumber;
    }
}
