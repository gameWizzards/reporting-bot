package com.telegram.reporting.repository;

import com.telegram.reporting.domain.SalaryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryReportRepository extends JpaRepository<SalaryReport, Long> {
}
