package com.telegram.reporting.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Telegram User entity.
 */
@Data
@Entity
@Table(name = "time_record")
@EqualsAndHashCode()
public class TimeRecord {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "minutes", nullable = false)
    private Integer minutes;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private Report report;

    @OneToOne
    private Category category;

}
