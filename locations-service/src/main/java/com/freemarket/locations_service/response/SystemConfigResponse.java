package com.freemarket.locations_service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigResponse {
    private Long id;
    private String configKey;
    private String configValue;
    private String description;
    private String countryName;
}