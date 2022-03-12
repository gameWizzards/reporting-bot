package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.CategoryRepository;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Category> getByName(String name) {
        Objects.requireNonNull(name, "Can't find category. Param 'name' is required");
        return Optional.ofNullable(categoryRepository.getByName(name));
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
