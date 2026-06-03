package com.freemarket.locations_service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con los datos de la ubicación del usuario por id de usuario")
public class LocationResponseForId {

    @Schema(description = "Dirección de la calle", example = "Av. Libertador Bernardo O'Higgins 1234")
    public String streetAddress;

    @Schema(description = "Nombre de la comuna", example = "Santiago")
    public String comunaNombre;

    @Schema(description = "Nombre de la región", example = "Región Metropolitana")
    public String regionNombre;

    public String addressType;

    public boolean active;

}
