package com.freemarket.auth_service.model;


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
@Table(name = "rol")
@AllArgsConstructor
@NoArgsConstructor
@Data

@Schema(description = "Entidad que representa los roles del sistema")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Schema(description = "ID del rol", example = "1")
    private Long rolId;

    @Column(nullable = false, length = 50)

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String rolName;

    @Column(nullable = false, length = 50)

    @Schema(description = "Descripción del rol", example = "Administrador del sistema")
    private String Description;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    @JsonIgnore
    List<User> users;
}

