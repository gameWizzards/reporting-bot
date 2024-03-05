package com.telegram.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor

@Data
@Entity
@Table(name = "employee_tariff", schema = "public")
public class EmployeeTariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "overridden_tariffication", nullable = false, scale = 2)
    private BigDecimal overriddenTariffication;
}
