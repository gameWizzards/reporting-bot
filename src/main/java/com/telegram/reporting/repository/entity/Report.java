package com.telegram.reporting.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Telegram User entity.
 */
@Data
@Entity
@Table(name = "report", schema = "public")
@EqualsAndHashCode()
public class Report {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date", nullable = false)
    private String date;

    @OneToOne
    private User user;

    @OneToMany
    private List<TimeRecord> timeRecords;
}
