package com.telegram.reporting.repository;

import com.telegram.reporting.repository.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT c FROM Category c WHERE c.nameKey=?1")
    Optional<Category> getByName(String name);

    @Query(value = "SELECT c FROM Category c WHERE c.id=?1")
    Optional<Category> getById(long id);

    @Query(value = "SELECT c FROM Category c WHERE c.deleted=false or c.deleted=?1")
    List<Category> getAll(boolean includeDeleted);
}
