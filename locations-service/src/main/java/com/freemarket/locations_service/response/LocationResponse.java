package com.freemarket.locations_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con los datos de la ubicación registrada o actualizada")
public class LocationResponse {

    @Schema(description = "ID de la ubicación", example = "1")
    public Long locationId;

    @Schema(description = "ID del usuario propietario de la ubicación", example = "42")
    public Long userId;

    @Schema(description = "Dirección de la calle", example = "Av. Libertador Bernardo O'Higgins 1234")
    public String streetAddress;

    @Schema(description = "Latitud de la ubicación", example = "-33.4569")
    public double latitude;

    @Schema(description = "Longitud de la ubicación", example = "-70.6483")
    public double longitude;

    @Schema(description = "Nombre de la comuna", example = "Santiago")
    public String comunaNombre;

    @Schema(description = "Nombre de la región", example = "Región Metropolitana")
    public String regionNombre;
}