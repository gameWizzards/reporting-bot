package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.TimeRecordRepository;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TimeRecordServiceImpl implements TimeRecordService {
    private final TimeRecordRepository timeRecordRepository;
    private final ReportService reportService;

    public TimeRecordServiceImpl(TimeRecordRepository timeRecordRepository,
                                 ReportService reportService) {
        this.timeRecordRepository = timeRecordRepository;
        this.reportService = reportService;
    }

    @Override
    public TimeRecord getById(Long id) {
        if (id == null) {
            return null;
        }
        return timeRecordRepository.getById(id);
    }

    @Override
    public TimeRecord save(TimeRecord timeRecord) {
        if (timeRecord == null) {
            return null;
        }
        return timeRecordRepository.saveAndFlush(timeRecord);
    }

    @Override
    public List<TimeRecordTO> getTimeRecordTOs(String date, Long chatId) {
        if (StringUtils.isBlank(date) || chatId == null) {
            return null;
        }
        LocalDate localDate = DateTimeUtils.parseDefaultDate(date);
        List<TimeRecord> trs = timeRecordRepository.getTimeRecordsByReportDateAndUserChatId(localDate, chatId);

        if (CollectionUtils.isEmpty(trs)) {
            return null;
        }

        return trs.stream()
                .map(TimeRecordTO::new)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByTimeRecordTO(TimeRecordTO timeRecordTO) {
        Validate.notNull(timeRecordTO, "Required not null TimeRecordTO object to remove time record");
        String errorMessage = "Required to have timeRecordId when make remove. %s".formatted(timeRecordTO);
        Validate.notNull(timeRecordTO.getId(), errorMessage);

        long timeRecordCount = timeRecordRepository.countTimeRecordsInReport(timeRecordTO.getReportId());
        boolean isLastElement = timeRecordCount == 1;

        if (isLastElement) {
            reportService.delete(timeRecordTO.getReportId());
            return;
        }

        timeRecordRepository.delete(timeRecordTO.getId());
    }
}
