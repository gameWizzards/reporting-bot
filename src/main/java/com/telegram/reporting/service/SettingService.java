package com.telegram.reporting.service;

import com.telegram.reporting.repository.entity.Setting;

import java.util.List;
import java.util.Optional;

public interface SettingService {

    Setting createSetting(String key, String value);

    Setting updateSetting(String key, String value);

    Optional<String> getValue(String key);

    List<Setting> getAllSettings();
}
