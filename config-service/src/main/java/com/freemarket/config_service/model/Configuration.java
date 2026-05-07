package com.freemarket.config_service.model;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuration")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Entidad que representa la configuración visual de un comercio")
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la configuración", example = "1")
    private Long idConfig;

    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre del comercio", example = "Mi Tienda Online")
    private String commerceName;

    @Column(nullable = false, unique = true)
    @Schema(description = "ID del usuario propietario del comercio", example = "42")
    private Long idUser;

    @Column(nullable = false)
    @Schema(description = "URL del logo del comercio", example = "https://cdn.mitienda.com/logo.png")
    private String LogoUrl;

    @Column(nullable = false)
    @Schema(description = "URL del favicon del comercio", example = "https://cdn.mitienda.com/favicon.ico")
    private String favicomUrl;

    @Column(nullable = false)
    @Schema(description = "Fuente principal del comercio", example = "Roboto")
    private String principalfont;

    @Column(nullable = false)
    @Schema(description = "Color primario en formato HEX", example = "#3A86FF")
    private String primarColor;

    @Column(nullable = false)
    @Schema(description = "Color secundario en formato HEX", example = "#FF006E")
    private String secondaryColor;

    @Column(nullable = false)
    @Schema(description = "Fecha de última actualización", example = "2026-01-15")
    private Date updateAt;
}
