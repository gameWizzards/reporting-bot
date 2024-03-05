package com.telegram.reporting.mapper;

import com.telegram.reporting.dto.CategoryTO;
import com.telegram.reporting.domain.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends BaseMapper<Category, CategoryTO>{
}
