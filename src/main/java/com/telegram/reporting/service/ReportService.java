package com.telegram.reporting.service;

import com.telegram.reporting.domain.Report;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    Report save(Report report);

    void delete(Long reportId);

    Report getReportByDateAndChatId(LocalDate date, Long chatId);

    List<Report> getReportsBelongMonth(LocalDate statisticDate, Long chatId);

}
