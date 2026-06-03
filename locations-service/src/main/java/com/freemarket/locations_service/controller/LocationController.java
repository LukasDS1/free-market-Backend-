package com.freemarket.locations_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.freemarket.locations_service.request.LocationRequest;
import com.freemarket.locations_service.response.LocationResponse;
import com.freemarket.locations_service.response.LocationResponseForId;
import com.freemarket.locations_service.service.LocationsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
        description = "Creates a new user location using geocoding service. The first location created is automatically set as active."
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
            description = "Invalid request data or address type already exists",
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
            description = "Location data. addressType must be CASA, TRABAJO or OTRO",
            required = true,
            content = @Content(schema = @Schema(implementation = LocationRequest.class))
        )
        @RequestBody LocationRequest request,
        @Parameter(description = "User ID", required = true, example = "1")
        @RequestHeader("X-User-Id") Long userId
    ) {
        request.setUserId(userId);
        return ResponseEntity.ok(locationsService.createUserLocation(request));
    }

    @PutMapping("/updateLocation")
    @Operation(
        summary = "Update location",
        description = "Updates an existing user location by addressType using geocoding service."
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
            description = "Location not found for given addressType",
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
            description = "Updated location data. addressType identifies which location to update.",
            required = true,
            content = @Content(schema = @Schema(implementation = LocationRequest.class))
        )
        @RequestBody LocationRequest request,
        @Parameter(description = "User ID", required = true, example = "1")
        @RequestHeader("X-User-Id") Long userId
    ) {
        request.setUserId(userId);
        return ResponseEntity.ok(locationsService.updateLocation(request));
    }

    @GetMapping("/getLocation/{id}")
    @Operation(
        summary = "Get active user location",
        description = "Returns the currently active location for a user. Used by delivery service."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Active location retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LocationResponseForId.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No active location found for user",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<LocationResponseForId> getLocationByUserId(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(locationsService.getLocationByUserId(id));
    }

    @GetMapping("/getLocations/{id}")
    @Operation(
        summary = "Get all user locations",
        description = "Returns all registered locations for a user. Used by the profile page."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Locations retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = LocationResponseForId.class))
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<LocationResponseForId>> getAllLocationsByUserId(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(locationsService.getAllLocationsByUserId(id));
    }

    @DeleteMapping("/deleteLocation")
    @Operation(
        summary = "Delete a location",
        description = "Deletes a user location by addressType. If the deleted location was active, the next available location is automatically set as active."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Location deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Location not found for given addressType",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Void> deleteLocation(
        @Parameter(description = "User ID", required = true, example = "1")
        @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "Address type to delete (CASA, TRABAJO, OTRO)", required = true, example = "CASA")
        @RequestParam String addressType
    ) {
        locationsService.deleteLocation(userId, addressType);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/setActive")
    @Operation(
        summary = "Set active location",
        description = "Marks a location as active by addressType. Used at checkout when the user has multiple locations. All other locations for this user are set as inactive."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Active location updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No locations found for user",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid addressType",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Void> setActiveLocation(
        @Parameter(description = "User ID", required = true, example = "1")
        @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "Address type to set as active (CASA, TRABAJO, OTRO)", required = true, example = "CASA")
        @RequestParam String addressType
    ) {
        locationsService.setActiveLocation(userId, addressType);
        return ResponseEntity.ok().build();
    }
}