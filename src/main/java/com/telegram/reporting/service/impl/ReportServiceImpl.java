package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.ReportRepository;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    @Override
    public Report save(Report report) {
        Validate.notNull(report, "Report object is required for save operation");
        return reportRepository.saveAndFlush(report);
    }

    @Override
    public void delete(Long id) {
        Validate.notNull(id, "Required reportId to make remove.");
        reportRepository.delete(id);
    }

    @Override
    public Report getReportByDateAndChatId(LocalDate date, Long chatId) {
        Validate.notNull(chatId, "ChatId is required to get the report!");
        Validate.notNull(date, "Date is required to get the report!");
        return reportRepository.getReportByDateAndChatId(date, chatId)
                .orElse(null);
    }

    @Override
    public List<Report> getReportsBelongMonth(LocalDate statisticDate, Long chatId) {
        Validate.notNull(chatId, "ChatId is required for get report!");
        Validate.notNull(statisticDate, "StatisticDate is required for get report!");
        return reportRepository.getReportsBelongMonth(statisticDate.getMonthValue(), statisticDate.getYear(), chatId);
    }
}
