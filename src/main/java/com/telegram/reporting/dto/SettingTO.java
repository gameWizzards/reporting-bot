package com.telegram.reporting.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class SettingTO {
    @NotBlank(message = "Key field is required, must not be blank")
    private final String key;
    @NotBlank(message = "Value field is required, must not be blank ")
    private final String value;
}
