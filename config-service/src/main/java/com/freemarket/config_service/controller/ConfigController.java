package com.freemarket.config_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.freemarket.config_service.request.ConfigRequest;
import com.freemarket.config_service.response.ConfigResponse;
import com.freemarket.config_service.service.configService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api-v1/config")
@RequiredArgsConstructor
@Tag(name = "Configuration Controller", description = "Endpoints for commerce visual configuration management")
public class ConfigController {

    private final configService configService;

    @GetMapping("/public")
    @Operation(summary = "Get public configuration", description = "Returns the visual configuration of the commerce without authentication")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigResponse.class))),
        @ApiResponse(responseCode = "404", description = "Configuration not found",
            content = @Content(examples = @ExampleObject(value = "No configuration found")))
    })
    public ResponseEntity<ConfigResponse> getPublicConfig() {
        return ResponseEntity.ok(configService.getPublicConfiguration());
    }

    @PostMapping("/create")
    @Operation(summary = "Create configuration", description = "Creates the initial visual configuration for a commerce")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Configuration created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or configuration already exists",
            content = @Content(examples = @ExampleObject(value = "A configuration already exists for this user"))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(examples = @ExampleObject(value = "User not found"))),
        @ApiResponse(responseCode = "503", description = "Service unavailable",
            content = @Content(examples = @ExampleObject(value = "Service is not available yet, try again later")))
    })
    public ResponseEntity<ConfigResponse> createConfig(@RequestBody ConfigRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configService.createConfiguration(request));
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Update configuration", description = "Partially updates the visual configuration of a commerce by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(examples = @ExampleObject(value = "Field cannot be empty"))),
        @ApiResponse(responseCode = "404", description = "Configuration not found",
            content = @Content(examples = @ExampleObject(value = "Configuration not found")))
    })
    public ResponseEntity<ConfigResponse> updateConfig(
            @Parameter(description = "Configuration ID to update", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody ConfigRequest request) {
        return ResponseEntity.ok(configService.updateConfiguration(id, request));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get configuration by user", description = "Returns the visual configuration associated to a user ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration found successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfigResponse.class))),
        @ApiResponse(responseCode = "404", description = "Configuration not found for this user",
            content = @Content(examples = @ExampleObject(value = "Configuration not found for this user")))
    })
    public ResponseEntity<ConfigResponse> getConfigurationByIdUser(
            @Parameter(description = "User ID", example = "42", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(configService.getConfigurationByIdUser(id));
    }
}