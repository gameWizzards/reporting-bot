package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Modifying
    @Query(value = "DELETE FROM Report r WHERE r.id=?1")
    void delete(Long reportId);
}
