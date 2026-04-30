package com.freemarket.locations_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.locations_service.request.LocationRequest;
import com.freemarket.locations_service.response.LocationResponse;
import com.freemarket.locations_service.service.LocationsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("api-v1/location")
@AllArgsConstructor
public class LocationController {


    private final LocationsService locationsService;


    @PostMapping("/createLocation")
    public ResponseEntity<LocationResponse> createLocation(@RequestBody LocationRequest request) {
    return ResponseEntity.ok().body(locationsService.createUserLocation(request)); 
    }
    

    


}
