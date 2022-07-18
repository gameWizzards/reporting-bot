package com.telegram.reporting.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "lock_month", nullable = false)
    private LocalDate lockMonth;

    public LockUpdateReport(Long userId, LocalDate lockMonth) {
        this.userId = userId;
        this.lockMonth = lockMonth;
    }
}
