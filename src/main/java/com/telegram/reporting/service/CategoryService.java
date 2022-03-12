package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<Category> getByName(String name);

    List<Category> getAll();
}
