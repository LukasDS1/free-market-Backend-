package com.freemarket.locations_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.freemarket.locations_service.request.LocationRequest;
import com.freemarket.locations_service.response.LocationResponse;
import com.freemarket.locations_service.response.LocationResponseForId;
import com.freemarket.locations_service.service.LocationsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api-v1/location")
@AllArgsConstructor
@Tag(
    name = "Location Controller",
    description = "Endpoints for user location management"
)
public class LocationController {

    private final LocationsService locationsService;

    @PostMapping("/createLocation")
    @Operation(
        summary = "Create location",
        description = "Creates a new user location using geocoding service"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Location created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LocationResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Map service unavailable",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<LocationResponse> createLocation(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Location data",
            required = true,
            content = @Content(
                schema = @Schema(implementation = LocationRequest.class)
            )
        )
        @RequestBody LocationRequest request,
        @RequestHeader("X-User-Id") Long userId
    ) {

        request.setUserId(userId);

        return ResponseEntity.ok(
            locationsService.createUserLocation(request)
        );
    }

    @PutMapping("/updateLocation")
    @Operation(
        summary = "Update location",
        description = "Updates a user's location using geocoding service"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Location updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LocationResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Location not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Map service unavailable",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<LocationResponse> updateLocation(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated location data",
            required = true,
            content = @Content(
                schema = @Schema(implementation = LocationRequest.class)
            )
        )
        @RequestBody LocationRequest request,
        @RequestHeader("X-User-Id") Long userId
    ) {

        request.setUserId(userId);

        return ResponseEntity.ok(
            locationsService.updateLocation(request)
        );
    }

    @GetMapping("/getLocation/{id}")
    @Operation(
        summary = "Get user location",
        description = "Returns a user's registered location"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Location retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = LocationResponseForId.class
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Location not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<LocationResponseForId> getLocationByUserId(

        @Parameter(
            description = "User ID",
            required = true,
            example = "1"
        )
        @PathVariable Long id
    ) {

        return ResponseEntity.ok(
            locationsService.getLocationByUserId(id)
        );
    }
}