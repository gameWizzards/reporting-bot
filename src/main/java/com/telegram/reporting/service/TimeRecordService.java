package com.telegram.reporting.service;

import com.telegram.reporting.dto.TimeRecordTO;
import com.telegram.reporting.domain.Report;
import com.telegram.reporting.domain.TimeRecord;

import java.util.List;
import java.util.Optional;

public interface TimeRecordService {
    Optional<TimeRecordTO> getById(Long id);

    TimeRecordTO update(TimeRecordTO timeRecordTO);

    List<TimeRecordTO> getTimeRecordTOs(String date, Long chatId);

    void deleteByTimeRecordTO(TimeRecordTO timeRecordTO);

    List<TimeRecord> convertToTimeRecordEntities(String timeRecordJson, Report report);
}
