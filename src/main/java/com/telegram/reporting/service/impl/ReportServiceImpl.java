package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.ReportRepository;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.service.ReportService;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
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
    public Report getReport(LocalDate date) {
        Validate.notNull(date, "Date object is required for get report");
        return reportRepository.getByDate(date);
    }

    @Override
    public List<Report> getReportsBelongMonth(int month) {
        return getReportsBelongMonth(month, LocalDate.now().getYear());
    }

    @Override
    public List<Report> getReportsBelongMonth(int month, int year) {
        if (month < 1 || year < 1) {
            throw new DateTimeException("Not valid arguments. They must be bigger than ZERO. Month = %d. Year = %d.".formatted(month, year));
        }
        return reportRepository.getReportsBelongMonth(month, year);
    }
}
