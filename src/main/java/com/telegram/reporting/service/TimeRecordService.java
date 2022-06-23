package com.telegram.reporting.service;

import com.telegram.reporting.repository.dto.TimeRecordTO;

import java.util.List;

public interface TimeRecordService {
    List<TimeRecordTO> getTimeRecordTOs(String date, String chatId);

    void deleteByTimeRecordTO(TimeRecordTO timeRecordTO);
}
