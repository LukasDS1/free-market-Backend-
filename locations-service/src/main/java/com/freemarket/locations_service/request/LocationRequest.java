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

    @Schema(description = "ID del usuario", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long userId;

    @Schema(description = "Nombre de la calle", example = "Calle Apolo", requiredMode = Schema.RequiredMode.REQUIRED)
    public String street;

    @Schema(description = "Número de la casa o departamento", example = "3009", requiredMode = Schema.RequiredMode.REQUIRED)
    public String streetNumber;

    @Schema(description = "Comuna", example = "Colina", requiredMode = Schema.RequiredMode.REQUIRED)
    public String comuna;

    @Schema(description = "Región", example = "Región Metropolitana", requiredMode = Schema.RequiredMode.REQUIRED)
    public String region;
}
