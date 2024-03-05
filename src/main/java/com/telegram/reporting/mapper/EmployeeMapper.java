package com.telegram.reporting.mapper;

import com.telegram.reporting.domain.Category;
import com.telegram.reporting.domain.User;
import com.telegram.reporting.dto.CategoryTO;
import com.telegram.reporting.dto.EmployeeTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper extends BaseMapper<User, EmployeeTO>{
}
