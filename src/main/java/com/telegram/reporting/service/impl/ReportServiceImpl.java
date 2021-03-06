package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.ReportRepository;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.service.ReportService;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report save(Report report) {
        Validate.notNull(report, "Report object is required for save operation");
        return reportRepository.saveAndFlush(report);
    }

    @Override
    public void delete(Long id) {
        Validate.notNull(id, "Required id to make remove.");
        reportRepository.delete(id);
    }

    @Override
    public Report getReport(LocalDate date, Long chatId) {
        if (date == null || chatId == null) {
            throw new NullPointerException("Date and chatId objects are required for get report!");
        }
        return reportRepository.getByDate(date, chatId);
    }

    @Override
    public List<Report> getReportsBelongMonth(LocalDate statisticDate, Long chatId) {
        Validate.notNull(chatId, "ChatId is required for get report!");
        Validate.notNull(statisticDate, "StatisticDate is required for get report!");
        return reportRepository.getReportsBelongMonth(statisticDate.getMonthValue(), statisticDate.getYear(), chatId);
    }
}
