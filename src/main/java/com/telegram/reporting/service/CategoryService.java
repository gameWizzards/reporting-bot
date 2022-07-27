package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.Category;

import java.util.List;

public interface CategoryService {

    Category getCategoryByName(String name);

    List<Category> getAll();
}
