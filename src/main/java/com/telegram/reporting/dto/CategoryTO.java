package com.telegram.reporting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryTO {
    private Long id;
    private String nameKey;
    private String descriptionKey;
    private boolean deleted;
}
