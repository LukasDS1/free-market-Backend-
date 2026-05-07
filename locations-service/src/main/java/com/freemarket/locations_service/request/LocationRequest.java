package com.freemarket.locations_service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos para crear o actualizar una ubicación de usuario")
public class LocationRequest {

    @Schema(description = "ID del usuario al que pertenece la ubicación", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long userId;

    @Schema(description = "Dirección o calle del usuario", example = "Av. Libertador Bernardo O'Higgins 1234", requiredMode = Schema.RequiredMode.REQUIRED)
    public String address;

    @Schema(description = "Comuna de la ubicación", example = "Santiago", requiredMode = Schema.RequiredMode.REQUIRED)
    public String comuna;

    @Schema(description = "Región de la ubicación", example = "Región Metropolitana", requiredMode = Schema.RequiredMode.REQUIRED)
    public String region;
}
