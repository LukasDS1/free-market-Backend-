package com.freemarket.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.request.CreateRolRequest;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.request.RolChangeRequest;
import com.freemarket.auth_service.request.UpdateRequest;
import com.freemarket.auth_service.response.AuthResponse;
import com.freemarket.auth_service.service.AuthService;
import lombok.AllArgsConstructor;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import com.freemarket.auth_service.service.RolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



@RestController
@AllArgsConstructor
@RequestMapping("api-v1/auth")
@Tag(name = "Authentication Controller",description = "Endpoints para autenticación, registro, actualización y manejo de usuarios")

public class AuthController {
private final AuthService authService;
    private final RolService rolService;

@PatchMapping("/setState/{id}")
@Operation(
    summary = "Cambiar estado del usuario",
    description = "Activa o desactiva el estado del usuario"
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "200",
        description = "Estado actualizado correctamente"
    ),

    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(
            examples = @ExampleObject(
                value = "User not found"
            )
        )
    ),

    @ApiResponse(
        responseCode = "503",
        description = "Servicio no disponible",
        content = @Content(
            examples = @ExampleObject(
                value = "Service is not avalible yet, try again later"
            )
        )
    )
})

public ResponseEntity<?> setState(@PathVariable Long id) {

    authService.setUserState(id);

    return ResponseEntity.ok().build();
}




@PostMapping("/register")

@Operation(
    summary = "Registrar usuario",
    description = "Crea un nuevo usuario y retorna JWT + Refresh Token"
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "200",
        description = "Usuario registrado correctamente"
    ),

    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(
            examples = {

                @ExampleObject(
                    name = "Username duplicado",
                    value = "Username already exists"
                ),

                @ExampleObject(
                    name = "Email duplicado",
                    value = "Email already exists"
                )
            }
        )
    ),

    @ApiResponse(
        responseCode = "503",
        description = "Servicio no disponible",
        content = @Content(
            examples = @ExampleObject(
                value = "Service is not avalible yet, try again later"
            )
        )
    )
})

public ResponseEntity<AuthResponse> createUser(
        @RequestBody RegisterRequest user) {

    return ResponseEntity.ok()
            .body(authService.registerUser(user));
}




@PostMapping("/login")

@Operation(
    summary = "Iniciar sesión",
    description = "Autentica un usuario y genera JWT"
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "200",
        description = "Login exitoso"
    ),

    @ApiResponse(
        responseCode = "401",
        description = "Credenciales inválidas",
        content = @Content(
            examples = @ExampleObject(
                value = "Invalid Credentials"
            )
        )
    ),

    @ApiResponse(
        responseCode = "503",
        description = "Servicio no disponible",
        content = @Content(
            examples = @ExampleObject(
                value = "Service is not avalible yet, try again later"
            )
        )
    )
})

public ResponseEntity<AuthResponse> login(
        @RequestBody LoginRequest request) {

    return ResponseEntity.ok()
            .body(authService.login(request));
}




@PatchMapping("/update")

@Operation(
    summary = "Actualizar usuario",
    description = "Actualiza campos específicos del usuario"
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "200",
        description = "Actualización exitosa"
    ),

    @ApiResponse(
        responseCode = "400",
        description = "Datos inválidos",
        content = @Content(
            examples = {

                @ExampleObject(
                    name = "Username inválido",
                    value = "Username cannot contain spaces"
                ),

                @ExampleObject(
                    name = "Email inválido",
                    value = "Invalid email format"
                )
            }
        )
    ),

    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(
            examples = @ExampleObject(
                value = "User not found"
            )
        )
    )
})

public ResponseEntity<Void> updateUser(
        @RequestBody UpdateRequest user,
        @RequestHeader ("X-User-Id") Long idUser) {

    authService.UpdateUser(user,idUser);

    return ResponseEntity.ok().build();
}




@GetMapping("/role/{id}")

@Operation(
    summary = "Buscar rol por ID",
    description = "Verifica si un rol existe en el sistema"
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada correctamente"
    ),

    @ApiResponse(
        responseCode = "404",
        description = "Rol no encontrado",
        content = @Content(
            examples = @ExampleObject(
                value = "Role not found"
            )
        )
    )
})

public ResponseEntity<Boolean> getRolById(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            rolService.existById(id));
}




@GetMapping("/{id}")

@Operation(
    summary = "Buscar usuario por ID",
    description = "Verifica si un usuario existe"
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada correctamente"
    ),

    @ApiResponse(
        responseCode = "404",
        description = "Usuario no encontrado",
        content = @Content(
            examples = @ExampleObject(
                value = "User not found"
            )
        )
    )
})

public ResponseEntity<Boolean> getById(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            authService.getByid(id));
}

@PostMapping("/refresh")

@Operation(
    summary = "Refrescar token JWT",
    description = "Genera un nuevo access token usando el refresh token"
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "200",
        description = "Token refrescado correctamente"
    ),

    @ApiResponse(
        responseCode = "401",
        description = "Refresh token inválido",
        content = @Content(
            examples = @ExampleObject(
                value = "Invalid refresh token"
            )
        )
    ),

    @ApiResponse(
        responseCode = "503",
        description = "Servicio no disponible",
        content = @Content(
            examples = @ExampleObject(
                value = "Service is not avalible yet, try again later"
            )
        )
    )
})

public ResponseEntity<AuthResponse> refresh(
        @RequestBody Map<String, String> body) {

    return ResponseEntity.ok(
            authService.refresh(body.get("refreshToken")));
}


@PostMapping("/logout")

@Operation( summary = "Cerrar sesión",description = "Invalida el refresh token del usuario")
@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Logout exitoso"),
@ApiResponse(responseCode = "401",description = "Refresh token inválido",content = @Content(examples = @ExampleObject(value = "Invalid refresh token")))
})

public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
   authService.logout(body.get("refreshToken"));
   return ResponseEntity.ok().build();
}

@PostMapping("/rol")
@Operation(summary = "Crear rol", description = "Crea un nuevo rol en el sistema")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Rol creado correctamente"),
    @ApiResponse(responseCode = "400", description = "Rol ya existe",
        content = @Content(examples = @ExampleObject(value = "Rol con nombre X ya existe")))
})
public ResponseEntity<Rol> createRol(@RequestBody CreateRolRequest request) {
    return ResponseEntity.ok(rolService.createRol(request));
}

@PatchMapping("/rol/change")
@Operation(summary = "Cambiar rol de usuario", description = "Asigna un nuevo rol a un usuario existente")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Rol cambiado correctamente"),
    @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado",
        content = @Content(examples = @ExampleObject(value = "User not found")))
})
public ResponseEntity<Void> changeUserRol(@RequestBody RolChangeRequest request) {
    rolService.changeUserRol(request);
    return ResponseEntity.ok().build();
}

}

