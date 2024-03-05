package com.telegram.reporting.service;

import com.telegram.reporting.dto.CategoryTO;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<CategoryTO> findById(Long id);

    List<CategoryTO> getAll(boolean includeDeleted);

    Optional<CategoryTO> getAvailableCategoryByName(String name);

    boolean isCategory(String name);

    CategoryTO update(CategoryTO category);

    void delete(Long id);

    List<CategoryTO> getAllWithExistedOverriddenTariffs(boolean includeDeleted);
}
