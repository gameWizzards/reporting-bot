package com.telegram.reporting.domain;

import com.telegram.reporting.listener.EventPublisher;
import com.telegram.reporting.listener.EventType;
import com.telegram.reporting.listener.event.ReportUpdateEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.Optional;

@Data
@Entity
@Table(name = "time_record", schema = "public")
@EqualsAndHashCode(exclude = "report")
@ToString(exclude = "report")
@NoArgsConstructor
public class TimeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "minutes", nullable = false)
    private Integer minutes;

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @OneToOne
    private Category category;

    public void setHours(Integer hours) {
        this.minutes = hours * 60;
    }

    public Integer getHours() {
        return Optional.ofNullable(this.minutes)
                .map(min -> min / 60)
                .orElse(0);
    }

    @PrePersist
    public void prePersist() {
        EventPublisher.INSTANCE
                .publishEvent(new ReportUpdateEvent(EventType.TIME_RECORD_CREATE, this));
    }

    @PreUpdate
    public void preUpdate() {
        EventPublisher.INSTANCE
                .publishEvent(new ReportUpdateEvent(EventType.TIME_RECORD_UPDATE, this));
    }

    @PreRemove
    public void preRemove() {
        EventPublisher.INSTANCE
                .publishEvent(new ReportUpdateEvent(EventType.TIME_RECORD_DELETE, this));
    }
}
