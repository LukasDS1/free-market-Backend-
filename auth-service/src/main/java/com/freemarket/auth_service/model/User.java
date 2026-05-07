package com.freemarket.auth_service.model;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freemarket.auth_service.enums.UserEnums;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data

@Schema(description = "Entidad que representa un usuario del sistema", name = "User")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Schema(description = "ID del usuario", example = "1")
    private Long userId;

    @Column(nullable = false, length = 50, unique = true)

    @Schema(description = "Correo del usuario", example = "admin@gmail.com")
    private String email;

    @Column(nullable = false, length = 255)

    @Schema(description = "Contraseña del usuario")
    private String password;

    @Column(nullable = false, length = 50, unique = true)

    @Schema(description = "Nombre de usuario", example = "luka123")
    private String username;

    @Column(nullable = false, length = 50)

    @Schema(description = "Nombre", example = "Luka")
    private String firstName;

    @Column(nullable = false, length = 50)

    @Schema(description = "Apellido", example = "Gonzalez")
    private String lastName;

    @Column(nullable = true, length = 20)

    @Schema(description = "Género", example = "Masculino")
    private String genre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    @Schema(description = "Estado del usuario", example = "ACTIVO")
    private UserEnums status = UserEnums.ACTIVO;

    @Column(nullable = true)
      @Schema(description = "Token de actualizacion")
    private String refreshToken;

    @Column(nullable = true)
      @Schema(description = "Fecha de expritacion del token de actualizacion", example = "2026-01-12")
    private Instant refreshTokenExpiry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idRol")
    @JsonIgnoreProperties("usuarios")

    @Schema(description = "Rol asociado al usuario")
    private Rol rol;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.getRolName()));
    }

     @Override
    public boolean isAccountNonExpired() {
       return true;
    }
     @Override
    public boolean isAccountNonLocked() {
       return true;
    }
    
     @Override
    public boolean isCredentialsNonExpired() {
       return true;
    }
    
     @Override
    public boolean isEnabled() {
       return true;
    }
    
    @Override
    public String getUsername() {
    return this.username;
    }
    @Override
    public String getPassword() {
    return this.password;
}
    
}
