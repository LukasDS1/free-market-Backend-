package com.freemarket.locations_service.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.locations_service.DTO.MapsDTO;
import com.freemarket.locations_service.model.Comuna;
import com.freemarket.locations_service.model.Location;
import com.freemarket.locations_service.model.Region;
import com.freemarket.locations_service.repository.ComunaRepository;
import com.freemarket.locations_service.repository.LocationRepository;
import com.freemarket.locations_service.repository.RegionRepository;
import com.freemarket.locations_service.request.LocationRequest;
import com.freemarket.locations_service.response.LocationResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationsService {

    private final LocationRepository locationRepo;


    private final GoogleMapsService MapService;

    private final ComunaRepository comunaRepo;

    private final RegionRepository regionRepo;

    @Autowired
    @Qualifier("restTemplate")
    private  RestTemplate restTemplate;

   

    //actualizar locacion 
    public LocationResponse updateLocation(LocationRequest request) {

    Location location = locationRepo.findByUserId(request.getUserId())
            .orElseThrow(() -> new RuntimeException());

    if (request.getAddress() != null && !request.getAddress().isEmpty()) {
        validationAddress(request.getAddress());
        MapsDTO map = MapService.geocode(request.getAddress()).join();
        location.setStreetAddress(map.getFormattedAddress());
        location.setLatitude(map.getLatitude());
        location.setLongitud(map.getLongitude());
    }

    Region region = location.getComuna().getRegion(); 
    if (request.getRegion() != null && !request.getRegion().isEmpty()) {
        validationRegion(request.getRegion());
        region = regionRepo.findByNombreRegion(request.getRegion())
                .orElseGet(() -> {
                    Region newRegion = new Region();
                    newRegion.setNombreRegion(request.getRegion());
                    return regionRepo.save(newRegion);
                });
    }

    Comuna comuna = location.getComuna(); 
    if (request.getComuna() != null && !request.getComuna().isEmpty()) {
        validationComuna(request.getComuna());
        Region finalRegion = region;
        comuna = comunaRepo.findByNombreComuna(request.getComuna())
                .orElseGet(() -> {
                    Comuna newComuna = new Comuna();
                    newComuna.setNombreComuna(request.getComuna());
                    newComuna.setRegion(finalRegion);
                    return comunaRepo.save(newComuna);
                });
    }

    location.setComuna(comuna);
    Location saved = locationRepo.save(location);

    LocationResponse response = new LocationResponse();
    response.setLocationId(saved.getLocationId());
    response.setUserId(saved.getUserId());
    response.setStreetAddress(saved.getStreetAddress());
    response.setLatitude(saved.getLatitude());
    response.setLongitude(saved.getLongitud());
    response.setComunaNombre(comuna.getNombreComuna());
    response.setRegionNombre(region.getNombreRegion());

    return response;
}



    
    //crear locacion
   public LocationResponse createUserLocation(LocationRequest request){

    validationAddress(request.getAddress());
    validationComuna(request.getComuna());
    validationRegion(request.getRegion());

    if (!GetUserById(request.getUserId()).join()) { 
        throw new RuntimeException();
    }

    MapsDTO map = MapService.geocode(request.getAddress()).join();

    Region region = regionRepo.findByNombreRegion(request.getRegion())
            .orElseGet(() -> {
                Region newRegion = new Region();
                newRegion.setNombreRegion(request.getRegion());
                return regionRepo.save(newRegion);
            });

    Comuna comuna = comunaRepo.findByNombreComuna(request.getComuna())
            .orElseGet(() -> {
                Comuna newComuna = new Comuna();
                newComuna.setNombreComuna(request.getComuna());
                newComuna.setRegion(region);
                return comunaRepo.save(newComuna);
            });

    Location location = new Location();
    location.setUserId(request.getUserId());
    location.setStreetAddress(map.getFormattedAddress()); 
    location.setLatitude(map.getLatitude());
    location.setLongitud(map.getLongitude());
    location.setComuna(comuna);

    Location saved = locationRepo.save(location);

    LocationResponse response = new LocationResponse();
    response.setLocationId(saved.getLocationId());
    response.setUserId(saved.getUserId());
    response.setStreetAddress(saved.getStreetAddress());
    response.setLatitude(saved.getLatitude());
    response.setLongitude(saved.getLongitud());
    response.setComunaNombre(comuna.getNombreComuna());
    response.setRegionNombre(region.getNombreRegion());

    return response;
}


    //Rest
    @CircuitBreaker(name = "authService", fallbackMethod = "getUserByIdFallback")
    @TimeLimiter(name = "authService")
    public CompletableFuture<Boolean> GetUserById(Long id) {
        String URL = "http://auth-service/api-v1/auth/{id}";
        return CompletableFuture.supplyAsync(() ->
            restTemplate.getForObject(URL, Boolean.class, id)
        );
    }

     public CompletableFuture<Boolean> getUserByIdFallback(Long id, Exception ex) {
        return CompletableFuture.completedFuture(false);
    }


    //validaciones 

    public void validationAddress(String address){
        if(address == null || address.isEmpty()){
            throw  new IllegalArgumentException();
        }
    }

    public void validationComuna(String comuna){
    if (comuna == null || comuna.isEmpty()){
        throw new IllegalArgumentException();
    }
}

public void validationRegion(String region){
    if (region == null || region.isEmpty()){
        throw new IllegalArgumentException();
    }
}


}