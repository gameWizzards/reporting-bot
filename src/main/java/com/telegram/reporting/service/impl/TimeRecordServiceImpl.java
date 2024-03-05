package com.telegram.reporting.service.impl;

import com.telegram.reporting.mapper.CategoryMapper;
import com.telegram.reporting.mapper.TimeRecordMapper;
import com.telegram.reporting.repository.TimeRecordRepository;
import com.telegram.reporting.dto.TimeRecordTO;
import com.telegram.reporting.domain.Category;
import com.telegram.reporting.domain.Report;
import com.telegram.reporting.domain.TimeRecord;
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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeRecordServiceImpl implements TimeRecordService {
    private final TimeRecordRepository timeRecordRepository;
    private final ReportService reportService;
    private final CategoryService categoryService;
    private final TimeRecordMapper timeRecordMapper;
    private final CategoryMapper categoryMapper;


    @Override
    public Optional<TimeRecordTO> getById(Long id) {
        Validate.notNull(id, "Id is required to getting the TimeRecord!");
        return timeRecordRepository.getTimeRecordById(id)
                .map(timeRecordMapper::toDto);
    }

    @Override
    public TimeRecordTO update(TimeRecordTO trTO) {
        Validate.notNull(trTO, "TimeRecordTO object is required to save entity!");
        TimeRecord toSave = timeRecordRepository.getTimeRecordById(trTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Can't find timeRecord by id=%s".formatted(trTO.getId())));

        toSave.setHours(trTO.getHours());
        toSave.setNote(trTO.getNote());

        if (!Objects.equals(toSave.getCategory().getNameKey(), trTO.getCategoryNameKey())) {

            Category category = categoryService.getAvailableCategoryByName(trTO.getCategoryNameKey())
                    .map(categoryMapper::toEntity)
                    .orElseThrow(() -> new NoSuchElementException("Can't find category by nameKey=%s".formatted(trTO.getCategoryNameKey())));
            toSave.setCategory(category);
        }

        TimeRecord saved = timeRecordRepository.saveAndFlush(toSave);
//        reportService.setLastUpdateTime(trTO.getReportId());
        return timeRecordMapper.toDto(saved);
    }

    @Override
    public List<TimeRecordTO> getTimeRecordTOs(String date, Long chatId) {
        Validate.notNull(chatId, "ChatId is required to get the TimeRecords!");
        Validate.notNull(date, "Date is required to get the TimeRecords!");
        LocalDate localDate = DateTimeUtils.parseDefaultDate(date);
        List<TimeRecord> trs = timeRecordRepository.getTimeRecordsByReportDateAndUserChatId(localDate, chatId);

        return timeRecordMapper.toDtos(trs);
    }

    @Override
    @Transactional
    public void deleteByTimeRecordTO(TimeRecordTO trTO) {
        Validate.notNull(trTO, "Required not null TimeRecordTO object to remove time record");
        Validate.notNull(trTO.getId(), "Required to have timeRecordId when make remove. %s".formatted(trTO));

        long timeRecordCount = timeRecordRepository.countTimeRecordsInReport(trTO.getReportId());
        boolean isLastElement = timeRecordCount == 1;

        timeRecordRepository.getTimeRecordById(trTO.getId())
                .ifPresent(timeRecordRepository::delete);
        timeRecordRepository.flush();

        if (isLastElement) {
            reportService.delete(trTO.getReportId());
        }
    }

    @Override
    public List<TimeRecord> convertToTimeRecordEntities(String timeRecordJson, Report report) {
        // TODO vary big possibility to change this method
        List<TimeRecordTO> trTOS = JsonUtils.deserializeListItems(timeRecordJson, TimeRecordTO.class);
        List<TimeRecord> entities = new ArrayList<>();
        for (TimeRecordTO trTO : trTOS) {
            TimeRecord timeRecord = timeRecordMapper.toEntity(trTO);

            Category category = categoryService.getAvailableCategoryByName(trTO.getCategoryNameKey())
                    .map(categoryMapper::toEntity)
                    .orElseThrow(() -> new NoSuchElementException("Can't find category by nameKey=%s".formatted(trTO.getCategoryNameKey())));

            timeRecord.setCategory(category);
            timeRecord.setReport(report);
            entities.add(timeRecord);
        }
        return entities;
    }
}
