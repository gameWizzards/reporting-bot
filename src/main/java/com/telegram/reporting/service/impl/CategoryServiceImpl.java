package com.telegram.reporting.service.impl;

import com.telegram.reporting.mapper.CategoryMapper;
import com.telegram.reporting.repository.CategoryRepository;
import com.telegram.reporting.dto.CategoryTO;
import com.telegram.reporting.domain.Category;
import com.telegram.reporting.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryTO update(CategoryTO categoryTO) {
        Objects.requireNonNull(categoryTO, "Can't update category. Param 'category' is required");
        Category toUpdate = categoryRepository.findById(categoryTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Can't find category to update with id = " + categoryTO.getId()));

        if (Objects.nonNull(categoryTO.getNameKey())) {
            toUpdate.setNameKey(categoryTO.getNameKey());
        }
        if (Objects.nonNull(categoryTO.getDescriptionKey())) {
            toUpdate.setDescriptionKey(categoryTO.getDescriptionKey());
        }
        if (toUpdate.isDeleted() != categoryTO.isDeleted()) {
            toUpdate.setDeleted(categoryTO.isDeleted());
        }

        return categoryMapper.toDto(toUpdate);
    }

    @Override
    public void delete(Long id) {
        Objects.requireNonNull(id, "Can't delete category. Param 'id' is required");
        Category toDelete = categoryRepository.findById((id))
                .orElseThrow(() -> new NoSuchElementException("Can't find category to delete with id = " + id));
        toDelete.setDeleted(true);
//        categoryRepository.save(toDelete);
    }

    @Override
    public Optional<CategoryTO> findById(Long id) {
        Objects.requireNonNull(id, "Can't find category. Param 'id' is required");
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto);
    }

    @Override
    public Optional<CategoryTO> getAvailableCategoryByName(String name) {
        Objects.requireNonNull(name, "Can't find category by name. Param 'name' is required");
        return categoryRepository.getByName(name)
                .filter(Predicate.not(Category::isDeleted))
                .map(categoryMapper::toDto);
    }

    @Override
    public List<CategoryTO> getAll(boolean includeDeleted) {
        return categoryRepository.getAll(includeDeleted).stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public boolean isCategory(String name) {
        return categoryRepository.getByName(name)
                .filter(Predicate.not(Category::isDeleted))
                .isPresent();
    }

    @Override
    public List<CategoryTO> getAllWithExistedOverriddenTariffs(boolean includeDeleted) {
        return categoryRepository.getAllWithExistedOverriddenTariffs(includeDeleted).stream()
                .map(categoryMapper::toDto)
                .toList();
    }
}
