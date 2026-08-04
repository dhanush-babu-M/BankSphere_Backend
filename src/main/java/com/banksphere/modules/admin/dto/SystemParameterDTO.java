package com.banksphere.modules.admin.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemParameterDTO {
    private String configKey;
    private String configValue;
    private String configType;
    private String description;
    private String module;
}
