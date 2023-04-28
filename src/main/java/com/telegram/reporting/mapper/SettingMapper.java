package com.telegram.reporting.mapper;

import com.telegram.reporting.repository.dto.SettingTO;
import com.telegram.reporting.repository.entity.Setting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingMapper extends BaseMapper<Setting, SettingTO> {

}