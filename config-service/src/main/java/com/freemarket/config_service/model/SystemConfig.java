package com.freemarket.config_service.model;

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
@Table(name = "system_config")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Entidad que representa la configuración de entorno del comercio")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la configuración", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "nombre de la llave de configuracion", example = "search_country")
    private String configKey;  

    @Column(nullable = false)
    @Schema(description = "valor de la config key ", example = "CL")
    private String configValue; 

    @Column(nullable = false)
    @Schema(description = "Nombre del país", example = "Chile")
    private String countryName;

    @Column(nullable = false)
    @Schema(description = "descripcion de la configuracion de país", example = "Codigo de pais para busqueda de ubicaciones")
    private String description;
}