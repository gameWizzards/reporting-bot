package com.telegram.reporting.repository.filter;

import lombok.Builder;

@Builder
public class CategoryFilter {
    private Long id;
    private String name;
}
