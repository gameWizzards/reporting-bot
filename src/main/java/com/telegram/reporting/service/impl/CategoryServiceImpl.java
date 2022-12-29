package com.telegram.reporting.service.impl;

import com.telegram.reporting.exception.MismatchCategoryException;
import com.telegram.reporting.repository.CategoryRepository;
import com.telegram.reporting.repository.entity.Category;
import com.telegram.reporting.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category update(Category category) {
        Category toUpdate = findById(category.getId());
        if (Objects.isNull(toUpdate)) {
            return null;
        }
        if (Objects.nonNull(category.getNameKey())) {
            toUpdate.setNameKey(category.getNameKey());
        }
        if (Objects.nonNull(category.getDescriptionKey())) {
            toUpdate.setDescriptionKey(category.getDescriptionKey());
        }
        if (toUpdate.isDeleted() != category.isDeleted()) {
            toUpdate.setDeleted(category.isDeleted());
        }

        return categoryRepository.save(toUpdate);
    }

    @Override
    public void delete(Long id) {
        Category toDelete = findById(id);
        if (Objects.isNull(toDelete)) {
            throw new IllegalArgumentException("Can't find category to delete with id = " + id);
        }
        toDelete.setDeleted(true);
        categoryRepository.save(toDelete);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.getById(id);
    }

    @Override
    public Category getCategoryByName(String name) {
        Objects.requireNonNull(name, "Can't find category. Param 'name' is required");
        return categoryRepository.getByName(name)
                .filter(Predicate.not(Category::isDeleted))
                .orElseThrow(() -> new MismatchCategoryException("Can't find category by name = %s".formatted(name)));
    }

    @Override
    public List<Category> getAll(boolean includeDeleted) {
         return categoryRepository.getAll(includeDeleted).stream()
                 .sorted(Comparator.comparing(Category::getId))
                 .toList();
    }

    @Override
    public boolean isCategory(String name) {
        return categoryRepository.getByName(name)
                .filter(Predicate.not(Category::isDeleted))
                .isPresent();
    }
}
