package com.freemarket.config_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.freemarket.config_service.model.SystemConfig;
import com.freemarket.config_service.service.SystemConfigService;

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
@RequestMapping("api-v1/config/system")
@RequiredArgsConstructor
@Tag(name = "System Config Controller", description = "Endpoints for global system configuration")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/country")
    @Operation(summary = "Get current search country", description = "Returns the current country used by Nominatim for geocoding")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SystemConfig.class))),
        @ApiResponse(responseCode = "404", description = "System config not found",
            content = @Content(examples = @ExampleObject(value = "System config not found")))
    })
    public ResponseEntity<SystemConfig> getCountry() {
        return ResponseEntity.ok(systemConfigService.getCountry());
    }

    @PatchMapping("/country/{countryCode}/{countryName}")
    @Operation(summary = "Update search country", description = "Changes the country where Nominatim will search for locations. Use ISO 3166-1 alpha-2 code (e.g. cl, ar, pe)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Country updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SystemConfig.class))),
        @ApiResponse(responseCode = "404", description = "System config not found",
            content = @Content(examples = @ExampleObject(value = "System config not found")))
    })
    public ResponseEntity<SystemConfig> updateCountry(
            @Parameter(description = "ISO 3166-1 alpha-2 country code", example = "cl", required = true)
            @PathVariable String countryCode,
            @Parameter(description = "Country name", example = "Chile", required = true)
            @PathVariable String countryName) {
        return ResponseEntity.ok(systemConfigService.updateCountry(countryCode, countryName));
    }
}