package com.telegram.reporting.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor


@Data
@Entity
@Table(name = "tariff", schema = "public")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "tariffication", nullable = false, scale = 2)
    private BigDecimal tariffication;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Singular
    @OneToMany(mappedBy = "tariff")
    private List<EmployeeTariff> overriddenTariffications;
}
