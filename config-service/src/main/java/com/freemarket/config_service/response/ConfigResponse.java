package com.freemarket.config_service.response;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Datos de respuesta con la configuración visual del comercio")
public class ConfigResponse {
    @Schema(description = "id de la configuracion", example = "Mi Tienda Online")
    public Long id;
    @Schema(description = "Nombre del comercio", example = "Mi Tienda Online")
    public String commerceName;

    @Schema(description = "URL del logo del comercio", example = "https://cdn.mitienda.com/logo.png")
    public String logoUrl;

    @Schema(description = "URL del favicon del comercio", example = "https://cdn.mitienda.com/favicon.ico")
    public String favicomUrl;

    @Schema(description = "Fuente principal del comercio", example = "Roboto")
    public String principalFont;

    @Schema(description = "Color primario en formato HEX", example = "#3A86FF")
    public String primaryColor;

    @Schema(description = "Color secundario en formato HEX", example = "#FF006E")
    public String secondaryColor;

    @Schema(description = "Fecha de última actualización", example = "2026-01-15")
    public Date updateDate;
}