package com.telegram.reporting.repository.dto;

import com.telegram.reporting.repository.entity.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public class SettingTO implements Serializable {
    @NotBlank(message = "Key field is required, must not be blank")
    private final String key;
    @NotBlank(message = "Value field is required, must not be blank ")
    private final String value;

    public SettingTO(Setting setting) {
        this.key = setting.getKey();
        this.value = setting.getValue();
    }
}
