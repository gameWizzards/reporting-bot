package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.TimeRecordRepository;
import com.telegram.reporting.repository.dto.TimeRecordTO;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.repository.entity.TimeRecord;
import com.telegram.reporting.service.CategoryService;
import com.telegram.reporting.service.ReportService;
import com.telegram.reporting.service.TimeRecordService;
import com.telegram.reporting.utils.DateTimeUtils;
import com.telegram.reporting.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeRecordServiceImpl implements TimeRecordService {
    private final TimeRecordRepository timeRecordRepository;
    private final ReportService reportService;
    private final CategoryService categoryService;


    @Override
    public TimeRecord getById(Long id) {
        Validate.notNull(id, "Id is required to getting the TimeRecord!");
        return timeRecordRepository.getTimeRecordById(id)
                .orElseThrow(() -> new RuntimeException("Can't find timeRecord by id=%s".formatted(id)));
    }

    @Override
    public TimeRecord save(TimeRecord timeRecord) {
        Validate.notNull(timeRecord, "TimeRecord object is required to save it!");
        return timeRecordRepository.saveAndFlush(timeRecord);
    }

    @Override
    public List<TimeRecordTO> getTimeRecordTOs(String date, Long chatId) {
        Validate.notNull(chatId, "ChatId is required to get the TimeRecords!");
        Validate.notNull(date, "Date is required to get the TimeRecords!");
        LocalDate localDate = DateTimeUtils.parseDefaultDate(date);
        List<TimeRecord> trs = timeRecordRepository.getTimeRecordsByReportDateAndUserChatId(localDate, chatId);

        return trs.stream()
                .map(TimeRecordTO::new)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByTimeRecordTO(TimeRecordTO timeRecordTO) {
        Validate.notNull(timeRecordTO, "Required not null TimeRecordTO object to remove time record");
        Validate.notNull(timeRecordTO.getId(), "Required to have timeRecordId when make remove. %s".formatted(timeRecordTO));

        long timeRecordCount = timeRecordRepository.countTimeRecordsInReport(timeRecordTO.getReportId());
        boolean isLastElement = timeRecordCount == 1;

        if (isLastElement) {
            reportService.delete(timeRecordTO.getReportId());
            return;
        }

        timeRecordRepository.delete(timeRecordTO.getId());
    }

    @Override
    public List<TimeRecord> convertToTimeRecordEntities(String timeRecordJson, Report report) {
        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);
        List<TimeRecord> entities = new ArrayList<>();
        for (TimeRecordTO trTO : trTOS) {
            Category category = categoryService.getCategoryByName(trTO.getCategoryNameKey());
            TimeRecord timeRecord = new TimeRecord(trTO);
            timeRecord.setId(trTO.getId());
            timeRecord.setCategory(category);
            timeRecord.setReport(report);
            entities.add(timeRecord);
        }
        return entities;
    }
}
