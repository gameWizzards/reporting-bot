package com.telegram.reporting.mapper;

import com.telegram.reporting.dto.SettingTO;
import com.telegram.reporting.domain.Setting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingMapper extends BaseMapper<Setting, SettingTO> {

}