package com.telegram.reporting.service.impl;

import com.telegram.reporting.repository.SettingRepository;
import com.telegram.reporting.repository.entity.Setting;
import com.telegram.reporting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository settingRepository;

    @Override
    public Setting createSetting(String key, String value) {
        validateArgs(key, value);
        if (settingRepository.findById(key).isPresent()) {
            throw new DuplicateKeyException("You try to create setting with existing key: %s".formatted(key));
        }
        return settingRepository.save(new Setting(key, value));
    }

    @Override
    public Setting updateSetting(String key, String value) {
        validateArgs(key, value);
        Setting setting = settingRepository.findById(key).orElseThrow(() -> new NoSuchElementException("Can't update setting '%s', it doesn't exist!".formatted(key)));
        setting.setValue(value);
        return settingRepository.save(setting);
    }

    @Override
    public Optional<String> getValue(String key) {
        if (StringUtils.isBlank(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(settingRepository.getValue(key));
    }

    @Override
    public List<Setting> getAllSettings() {
        return settingRepository.findAll().stream()
                .sorted(Comparator.comparing(Setting::getKey))
                .toList();
    }

    private void validateArgs(String key, String value) {
        Validate.notBlank(key, "Key is required for setting");
        Validate.notBlank(value, "Value is required for setting");
    }
}
