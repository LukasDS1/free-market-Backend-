package com.freemarket.privileges_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rolPrivileges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa la relación entre un rol y sus privilegios asignados")
public class rolPrivileges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del registro", example = "1")
    private Long id;

    @Schema(description = "ID del rol al que se asigna el privilegio", example = "3")
    private Long roleId;

    @ManyToOne
    @JoinColumn(name = "privilegesId")
    @Schema(description = "Privilegio asignado al rol")
    private Privileges privilege;
}