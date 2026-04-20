package com.freemarket.locations_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.locations_service.DTO.MapsDTO;
import com.freemarket.locations_service.model.Comuna;
import com.freemarket.locations_service.model.Location;
import com.freemarket.locations_service.model.Region;
import com.freemarket.locations_service.repository.ComunaRepository;
import com.freemarket.locations_service.repository.LocationBodegaRepository;
import com.freemarket.locations_service.repository.LocationRepository;
import com.freemarket.locations_service.repository.RegionRepository;
import com.freemarket.locations_service.request.LocationRequest;
import com.freemarket.locations_service.response.LocationBodegaResponse;
import com.freemarket.locations_service.response.LocationResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationsService {

    private final LocationRepository locationRepo;

    private final LocationBodegaRepository locationBodRepo;

    private final GoogleMapsService MapService;

    private final ComunaRepository comunaRepo;

    private final RegionRepository regionRepo;

    private final RestTemplate restTemplate;

    


    public LocationResponse createUserLocation(LocationRequest request){


    validationAddress(request.getAddress());

    if (!GetUserById(request.getUserId())) {
        throw new RuntimeException();
    }

    MapsDTO map = MapService.geocode(request.getAddress());

   
    Region region = regionRepo.findByNombreRegion(map.getRegionNombre())
            .orElseGet(() -> {Region newRegion = new Region();
                newRegion.setNombreRegion(map.getRegionNombre());
                return regionRepo.save(newRegion);
            });

    Comuna comuna = comunaRepo.findByNombreComuna(map.getComunaNombre()).orElseGet(() -> {
                Comuna newComuna = new Comuna();
                newComuna.setNombreComuna(map.getComunaNombre());
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
    private boolean GetUserById(Long id){

        String URL = "http://auth-service/api-v1/auth/{id}";

        return restTemplate.getForObject( URL, boolean.class,id);
    }


    //validaciones 

    public void validationAddress(String address){
        if(address == null || address.isEmpty()){
            throw  new IllegalArgumentException();
        }
    }


}
