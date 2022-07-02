package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Modifying
    @Query(value = "DELETE FROM Report r WHERE r.id=?1")
    void delete(Long reportId);

    @Query(value = "SELECT r FROM Report r JOIN FETCH r.timeRecords WHERE r.date=?1 AND r.user.chatId=?2")
    Report getByDate(LocalDate date, Long chatId);

    @Query(value = "SELECT DISTINCT(r) FROM Report r JOIN FETCH r.timeRecords " +
            "WHERE extract(month from r.date)=?1 AND extract(year from r.date)=?2 AND r.user.chatId=?3 " +
            "ORDER BY r.date")
    List<Report> getReportsBelongMonth(int month, int year, Long chatId);
}
