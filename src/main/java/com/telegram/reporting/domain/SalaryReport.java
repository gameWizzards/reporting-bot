package com.telegram.reporting.domain;

import com.telegram.reporting.utils.convertor.YearMonthAttributeConvertor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor

@Data
@Entity
@Table(name = "salary_report", schema = "public")
@EqualsAndHashCode(exclude = {"employee", "categoryPayments"})
public class SalaryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Convert(converter = YearMonthAttributeConvertor.class)
    @Column(name = "period", nullable = false)
    private YearMonth period;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @OneToMany(mappedBy = "salaryReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryPayment> categoryPayments;

    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    @Column(name = "created", updatable = false, nullable = false)
    private LocalDateTime created;

    // TODO: consider what think is update - change salary or changes in category payments
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

}
