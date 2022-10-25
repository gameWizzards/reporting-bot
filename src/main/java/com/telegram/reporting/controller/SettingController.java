package com.telegram.reporting.controller;

import com.telegram.reporting.repository.dto.SettingTO;
import com.telegram.reporting.repository.entity.Setting;
import com.telegram.reporting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Validated
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public SettingTO getSetting(@Valid @RequestParam("key") @NotBlank(message = "Request param 'key' must not be blank") String key) {
        Optional<String> value = settingService.getValue(key);
        return new SettingTO(key, value.orElseThrow(() -> new NoSuchElementException("Setting not found by key - '%s'".formatted(key))));
    }

    @GetMapping("/all")
    public List<SettingTO> getAllSettings() {
        return settingService.getAllSettings().stream()
                .map(SettingTO::new)
                .toList();
    }

    @PostMapping
    public SettingTO createSetting(@Valid @RequestBody SettingTO settingTO) {
        settingService.createSetting(settingTO.getKey(), settingTO.getValue());
        return settingTO;
    }

    @PatchMapping
    public SettingTO updateSettingValue(@Valid @RequestParam("key") @NotBlank(message = "Request param 'key' must not be blank") String key,
                                        @Valid @RequestParam("value") @NotBlank(message = "Request param 'value' must not be blank") String value) {
        Setting updated = settingService.updateSetting(key, value);
        return new SettingTO(updated);
    }
}
