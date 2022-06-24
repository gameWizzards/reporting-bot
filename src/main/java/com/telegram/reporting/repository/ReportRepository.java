package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Modifying
    @Query(value = "DELETE FROM Report r WHERE r.id=?1")
    void delete(Long reportId);

    @Query(value = "SELECT r FROM Report r JOIN FETCH r.timeRecords WHERE r.date=?1")
    Report getByDate(LocalDate date);
}
