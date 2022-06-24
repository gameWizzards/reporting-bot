package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.Report;

import java.time.LocalDate;

public interface ReportService {

    Report save(Report report);

    void delete(Long reportId);

    Report getReport(LocalDate date);

}
