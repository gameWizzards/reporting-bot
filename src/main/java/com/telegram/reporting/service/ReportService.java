package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.Report;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    Report save(Report report);

    void delete(Long reportId);

    Report getReport(LocalDate date, Long chatId);

    List<Report> getReportsBelongMonth(LocalDate statisticDate, Long chatId);

}
