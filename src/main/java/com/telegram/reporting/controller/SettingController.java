package com.telegram.reporting.controller;

import com.telegram.reporting.dto.SettingTO;
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

@Validated
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public SettingTO getSetting(@Valid @RequestParam("key") @NotBlank(message = "Request param 'key' must not be blank") String key) {
        return settingService.getByKey(key)
                .orElseThrow(() -> new NoSuchElementException("Setting not found by key - '%s'".formatted(key)));
    }

    @GetMapping("/all")
    public List<SettingTO> getAllSettings() {
        return settingService.getAllSettings();
    }

    @PostMapping
    public SettingTO createSetting(@Valid @RequestBody SettingTO settingTO) {
        return settingService.createSetting(settingTO);
    }

    @PatchMapping
    public SettingTO updateSettingValue(@Valid @RequestBody SettingTO settingTO) {
        return settingService.updateSetting(settingTO);
    }
}
