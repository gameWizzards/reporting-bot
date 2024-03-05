package com.telegram.reporting.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "lock_update_report", schema = "public")
public class LockUpdateReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    // TODO: change to mapping
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // TODO: change to YearMonth, using converter
    @Column(name = "lock_month", nullable = false)
    private LocalDate lockMonth;

    public LockUpdateReport(Long userId, LocalDate lockMonth) {
        this.userId = userId;
        this.lockMonth = lockMonth;
    }
}
