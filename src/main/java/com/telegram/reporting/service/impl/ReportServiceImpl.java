package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.ReportRepository;
import com.telegram.reporting.repository.entity.Report;
import com.telegram.reporting.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report save(Report report) {
        Objects.requireNonNull(report, "Report object is required for save operation");
        return reportRepository.saveAndFlush(report);
    }
}
