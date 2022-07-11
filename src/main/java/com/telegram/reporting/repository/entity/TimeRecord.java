package com.telegram.reporting.repository.entity;

import com.telegram.reporting.repository.dto.TimeRecordTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Entity
@Table(name = "time_record", schema = "public")
@EqualsAndHashCode(exclude = "report")
@ToString(exclude = "report")
public class TimeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "minutes", nullable = false)
    private Integer minutes;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @OneToOne
    private Category category;

    public TimeRecord() {
    }

    public TimeRecord(TimeRecordTO to) {
        setHours(to.getHours());
        this.created = to.getCreated();
        this.note = to.getNote();
    }

    public void setHours(Integer hours) {
        this.minutes = hours * 60;
    }

    public Integer getHours() {
        return Optional.ofNullable(this.minutes)
                .map(min -> min / 60)
                .orElse(0);
    }
}
