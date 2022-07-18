package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.LockUpdateReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LockUpdateReportRepository extends JpaRepository<LockUpdateReport, Long> {
    @Modifying
    @Query(value = """
            DELETE FROM LockUpdateReport l WHERE l.userId=?1
            AND extract(month from l.lockMonth)=?2 AND extract(year from l.lockMonth)=?3""")
    void delete(Long userId, int month, int year);

    @Query(value = """
            SELECT CASE WHEN COUNT(l)>0
                        THEN true
                        ELSE false
                    END
            FROM LockUpdateReport l
            WHERE l.userId=?1 AND extract(month from l.lockMonth)=?2 AND extract(year from l.lockMonth)=?3""")
    boolean lockExist(Long userId, int month, int year);
}