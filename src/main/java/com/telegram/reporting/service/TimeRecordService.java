package com.telegram.reporting.service;

import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.TimeRecord;

import java.util.List;

public interface TimeRecordService {
    TimeRecord getById(Long id);

    TimeRecord save(TimeRecord timeRecord);

    List<TimeRecordTO> getTimeRecordTOs(String date, String chatId);

    void deleteByTimeRecordTO(TimeRecordTO timeRecordTO);
}
