package com.telegram.reporting.repository;

import com.telegram.reporting.domain.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeRecordRepository extends JpaRepository<TimeRecord, Long> {

    @Query(value = "SELECT tr FROM TimeRecord tr JOIN FETCH tr.report WHERE tr.report.date=?1 and tr.report.user.chatId=?2")
    List<TimeRecord> getTimeRecordsByReportDateAndUserChatId(LocalDate reportDate, Long chatId);

    @Query(value = "SELECT count(*) FROM TimeRecord tr WHERE tr.report.id=?1")
    long countTimeRecordsInReport(Long reportId);

    @Modifying
    @Query(value = "DELETE FROM TimeRecord tr WHERE tr.id=?1")
    void delete(Long reportId);

    @Query(value = "SELECT tr FROM TimeRecord tr WHERE tr.id=?1")
    Optional<TimeRecord> getTimeRecordById(Long reportId);
}
