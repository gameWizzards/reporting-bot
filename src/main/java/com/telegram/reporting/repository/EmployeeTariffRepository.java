package com.telegram.reporting.repository;

import com.telegram.reporting.domain.EmployeeTariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeTariffRepository extends JpaRepository<EmployeeTariff, Long> {

    @Query("""
                    SELECT count(*) > 0
                    FROM EmployeeTariff et
                    WHERE et.employee.deleted = false
            """)
    boolean existsAnyOverriddenTariffs();

    @Query("""
                    SELECT et
                    FROM EmployeeTariff et
                    WHERE et.tariff.category.nameKey = :categoryNameKey
            """)
    List<EmployeeTariff> getOverriddenTariffsByCategory(String categoryNameKey);

    @Query("""
                    SELECT et
                    FROM EmployeeTariff et
                    WHERE et.employee.deleted = :includeDeleted
            """)
    List<EmployeeTariff> getEmployeeTariffWithExistedOverriddenTariffs(boolean includeDeleted);
}
