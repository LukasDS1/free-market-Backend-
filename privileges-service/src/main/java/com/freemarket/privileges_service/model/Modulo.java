package com.freemarket.privileges_service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modulo")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Entidad que representa un módulo del sistema al que se asocian privilegios")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del módulo", example = "1")
    private Long moduloId;

    @Column(nullable = false, length = 50, unique = true)
    @Schema(description = "Nombre del módulo", example = "Inventario")
    private String moduloname;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL)
    @JsonIgnore
    @Schema(hidden = true)
    List<Privileges> privileges;
}