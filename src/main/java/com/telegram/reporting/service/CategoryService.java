package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.Category;

import java.util.List;

public interface CategoryService {

    Category findById(Long id);

    List<Category> getAll(boolean includeDeleted);

    Category getCategoryByName(String name);

    boolean isCategory(String name);

    Category update(Category category);

    void delete(Long id);
}
