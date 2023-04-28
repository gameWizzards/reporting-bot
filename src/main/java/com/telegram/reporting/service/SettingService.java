package com.telegram.reporting.service;

import com.telegram.reporting.repository.dto.SettingTO;

import java.util.List;
import java.util.Optional;

public interface SettingService {

    SettingTO createSetting(SettingTO settingTO);

    SettingTO updateSetting(SettingTO settingTO);

    Optional<SettingTO> getByKey(String key);

    List<SettingTO> getAllSettings();
}
