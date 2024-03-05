package com.telegram.reporting.repository;

import com.telegram.reporting.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT c FROM Category c WHERE c.nameKey=:name")
    Optional<Category> getByName(String name);

    @Query(value = "SELECT c FROM Category c WHERE c.id=:id")
    Optional<Category> getById(long id);

    @Query(value = "SELECT c FROM Category c WHERE c.deleted=false or c.deleted=:includeDeleted")
    List<Category> getAll(boolean includeDeleted);

    @Query("""
            SELECT c 
            FROM Category c
            WHERE c.deleted= :includeDeleted
            AND c.id IN (
                SELECT t.category.id 
                FROM Tariff t
                JOIN t.overriddenTariffications ot
                WHERE t.overriddenTariffications.size > 0
                AND ot.employee.deleted=false
                )
                """)
    List<Category> getAllWithExistedOverriddenTariffs(boolean includeDeleted);
}
