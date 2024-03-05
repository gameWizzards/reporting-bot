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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor

@Data
@Entity
@Table(name = "category_payment", schema = "public")
public class CategoryPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false, updatable = false, unique = true)
    private Category category;

    @Column(name = "calculation_tariff", nullable = false)
    private BigDecimal calculationTariff;

    @Column(name = "worked_minutes", nullable = false)
    private Integer workedMinutes;

    @Column(name = "payment", nullable = false)
    private BigDecimal payment;

    @ManyToOne
    @JoinColumn(name = "salary_report_id", nullable = false)
    private SalaryReport salaryReport;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
}
