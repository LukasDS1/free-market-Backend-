package com.freemarket.locations_service.DTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO con datos de geolocalización retornados por el servicio de mapas")
public class MapsDTO {

    @Schema(description = "Latitud de la ubicación", example = "-33.4569")
    public double latitude;

    @Schema(description = "Longitud de la ubicación", example = "-70.6483")
    public double longitude;

    @Schema(description = "Dirección formateada completa", example = "Av. Libertador Bernardo O'Higgins 1234, Santiago")
    public String formattedAddress;

    @Schema(description = "Nombre de la comuna", example = "Santiago")
    public String comunaNombre;

    @Schema(description = "Nombre de la región", example = "Región Metropolitana")
    public String regionNombre;
}