package com.telegram.reporting.repository;

import com.telegram.reporting.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Modifying
    @Query(value = "DELETE FROM Report r WHERE r.id=?1")
    void delete(Long reportId);

    @Query(value = "SELECT r FROM Report r JOIN FETCH r.timeRecords WHERE r.date=?1 AND r.user.chatId=?2")
    Optional<Report> getReportByDateAndChatId(LocalDate date, Long chatId);

    @Query(value = "SELECT DISTINCT(r) FROM Report r JOIN FETCH r.timeRecords " +
                   "WHERE extract(month from r.date)=?1 AND extract(year from r.date)=?2 AND r.user.chatId=?3 " +
                   "ORDER BY r.date")
    List<Report> getReportsBelongMonth(int month, int year, Long chatId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Report r SET r.lastUpdate=?2 WHERE r.id=?1")
    void setLastUpdateTime(Long reportId, LocalDateTime lastUpdateTime);
}
