package com.telegram.reporting.service;

import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;

import java.util.List;

public interface TimeRecordService {
    TimeRecord getById(Long id);

    TimeRecord save(TimeRecord timeRecord);

    List<TimeRecordTO> getTimeRecordTOs(String date, Long chatId);

    void deleteByTimeRecordTO(TimeRecordTO timeRecordTO);

    List<TimeRecord> convertToTimeRecordEntities(String timeRecordJson, Report report);
}
