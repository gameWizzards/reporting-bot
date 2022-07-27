package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT c FROM Category c WHERE c.name=?1")
    Optional<Category> getByName(String name);

    @Query(value = "SELECT c FROM Category c WHERE c.id=?1")
    Optional<Category> getById(long id);
}
