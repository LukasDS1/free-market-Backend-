package com.freemarket.locations_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con la configuración del sistema")
public class SystemConfigResponse {
    @Schema(description = "ID de la configuración", example = "1")
    private Long id;
    @Schema(description = "Clave de la configuración", example = "search_country")
    private String configKey;
    @Schema(description = "Valor de la configuración", example = "cl")
    private String configValue;
    @Schema(description = "Descripción de la configuración", example = "País usado por Nominatim para geocodificación")
    private String description;
    @Schema(description = "Nombre del país configurado", example = "Chile")
    private String countryName;
}