package com.freemarket.privileges_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.privileges_service.request.AsignarPrivilegioRequest;
import com.freemarket.privileges_service.request.PrivilegeRequest;
import com.freemarket.privileges_service.request.moduloRequest;
import com.freemarket.privileges_service.response.ResponseDTO;
import com.freemarket.privileges_service.response.moduloResponse;
import com.freemarket.privileges_service.service.Services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api-v1/privileges")
@AllArgsConstructor
@Tag(
    name = "Privileges Controller",
    description = "Endpoints for module and role privilege management"
)
public class Controller {

    private final Services services;

    @PostMapping("/modules")
    @Operation(
        summary = "Create module",
        description = "Creates a new system module that can contain privileges"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Module created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = moduloResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Module already exists",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<moduloResponse> createModulo(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Module data",
            required = true,
            content = @Content(
                schema = @Schema(implementation = moduloRequest.class)
            )
        )
        @RequestBody moduloRequest request
    ) {

        moduloResponse response = services.createModulo(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{id}")
    @Operation(
        summary = "Get privileges by role",
        description = "Returns all privileges assigned to a role"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Privileges retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Role not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Service unavailable",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<ResponseDTO>> getPrivilegesByRole(
        @Parameter(
            description = "Role ID",
            example = "2",
            required = true
        )
        @PathVariable Long id
    ) {

        List<ResponseDTO> response = services.getPrivilegesByRole(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/modules")
    @Operation(
        summary = "Get all modules",
        description = "Returns all system modules"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Modules retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = moduloResponse.class)
            )
        )
    })
    public ResponseEntity<List<moduloResponse>> getAllModulos() {

        return ResponseEntity.ok(services.getAllModulos());
    }

    @GetMapping("/all")
    @Operation(
        summary = "Get all privileges",
        description = "Returns all system privileges"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Privileges retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseDTO.class)
            )
        )
    })
    public ResponseEntity<List<ResponseDTO>> getAllPrivileges() {

        return ResponseEntity.ok(services.getAllPrivileges());
    }

    @PostMapping("/create")
    @Operation(
        summary = "Create privilege",
        description = "Creates a privilege and associates it with a module"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Privilege created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or privilege already exists",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Module not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<ResponseDTO> createPrivilege(

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Privilege data",
            required = true,
            content = @Content(
                schema = @Schema(implementation = PrivilegeRequest.class)
            )
        )
        @RequestBody PrivilegeRequest request
    ) {

        return ResponseEntity.ok(services.createPrivilege(request));
    }

    @PostMapping("/asignar")
    @Operation(
        summary = "Assign privilege to role",
        description = "Assigns an existing privilege to a role"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Privilege assigned successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Role already has this privilege",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Role or privilege not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Service unavailable",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Void> asignarPrivilegio(

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Privilege assignment data",
            required = true,
            content = @Content(
                schema = @Schema(implementation = AsignarPrivilegioRequest.class)
            )
        )
        @RequestBody AsignarPrivilegioRequest request
    ) {

        services.asignarPrivilegioARol(request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/eliminar/{roleId}/{privilegeId}")
    @Operation(
        summary = "Remove privilege from role",
        description = "Removes a privilege assignment from a role"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Privilege removed successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Role does not have this privilege",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Role or privilege not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Void> eliminarPrivilegio(

        @Parameter(
            description = "Role ID",
            example = "2",
            required = true
        )
        @PathVariable Long roleId,

        @Parameter(
            description = "Privilege ID",
            example = "5",
            required = true
        )
        @PathVariable Long privilegeId
    ) {

        services.eliminarPrivilegioDeRol(roleId, privilegeId);

        return ResponseEntity.ok().build();
    }
}