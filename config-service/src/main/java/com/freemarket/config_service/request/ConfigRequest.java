package com.freemarket.config_service.request;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Datos requeridos para crear o actualizar la configuración de un comercio")
public class ConfigRequest {

    @Schema(description = "ID del usuario propietario del comercio", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long idUser;

    @Schema(description = "Nombre del comercio", example = "Mi Tienda Online", requiredMode = Schema.RequiredMode.REQUIRED)
    public String commerceName;

    @Schema(description = "URL del logo del comercio", example = "https://cdn.mitienda.com/logo.png", requiredMode = Schema.RequiredMode.REQUIRED)
    public String logoUrl;

    @Schema(description = "URL del favicon del comercio", example = "https://cdn.mitienda.com/favicon.ico", requiredMode = Schema.RequiredMode.REQUIRED)
    public String favicomUrl;

    @Schema(description = "Fuente principal del comercio", example = "Roboto", requiredMode = Schema.RequiredMode.REQUIRED)
    public String principalFont;

    @Schema(description = "Color primario en formato HEX", example = "#3A86FF", requiredMode = Schema.RequiredMode.REQUIRED)
    public String primaryColor;

    @Schema(description = "Color secundario en formato HEX", example = "#FF006E", requiredMode = Schema.RequiredMode.REQUIRED)
    public String secondaryColor;

    @Schema(description = "Fecha de última actualización", example = "2026-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    public Date updateAt;
}