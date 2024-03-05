package com.telegram.reporting.service.impl;

import com.telegram.reporting.mapper.SettingMapper;
import com.telegram.reporting.repository.SettingRepository;
import com.telegram.reporting.dto.SettingTO;
import com.telegram.reporting.domain.Setting;
import com.telegram.reporting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {
    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;

    @Override
    public SettingTO createSetting(@Valid SettingTO settingTO) {

        Optional<Setting> setting = settingRepository.findById(settingTO.getKey());
        setting.ifPresent(s -> {
            throw new DuplicateKeyException("You try to create setting with existing key: %s".formatted(s.getKey()));
        });
        settingRepository.save(settingMapper.toEntity(settingTO));

        return settingTO;
    }

    @Override
    public SettingTO updateSetting(SettingTO settingTO) {
        Setting setting = settingRepository.findById(settingTO.getKey())
                .orElseThrow(() -> new NoSuchElementException("Can't update setting '%s', it doesn't exist!".formatted(settingTO.getKey())));

        setting.setValue(settingTO.getValue());
        settingRepository.save(setting);

        return settingTO;
    }

    @Override
    public Optional<SettingTO> getByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return Optional.empty();
        }
        return settingRepository.getByKey(key)
                .map(settingMapper::toDto);
    }

    @Override
    public List<SettingTO> getAllSettings() {
        return settingRepository.findAll().stream()
                .sorted(Comparator.comparing(Setting::getKey))
                .map(settingMapper::toDto)
                .toList();
    }
}
