package com.freemarket.locations_service.service;

import org.springframework.stereotype.Service;
import com.freemarket.locations_service.DTO.MapsDTO;
import com.freemarket.locations_service.client.AuthClient;
import com.freemarket.locations_service.excepcion.ServiceUnavailableException;
import com.freemarket.locations_service.model.Comuna;
import com.freemarket.locations_service.model.Location;
import com.freemarket.locations_service.model.Region;
import com.freemarket.locations_service.repository.ComunaRepository;
import com.freemarket.locations_service.repository.LocationRepository;
import com.freemarket.locations_service.repository.RegionRepository;
import com.freemarket.locations_service.request.LocationRequest;
import com.freemarket.locations_service.response.LocationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationsService {

     private final LocationRepository locationRepo;
    private final GoogleMapsService mapService;
    private final ComunaRepository comunaRepo;
    private final RegionRepository regionRepo;
    private final AuthClient authClientService; 

    public LocationResponse createUserLocation(LocationRequest request) {
        validationAddress(request.getAddress());
        validationComuna(request.getComuna());
        validationRegion(request.getRegion());
        
        Boolean userExists = authClientService.getUserById(request.getUserId());
    if (userExists == null) {
        throw new ServiceUnavailableException("Service is unavalible");
    }
    if (!userExists) {
        throw new IllegalArgumentException("Usuario no encontrado");
    }

        MapsDTO map = mapService.geocode(request.getAddress());

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

    public LocationResponse updateLocation(LocationRequest request) {
        Location location = locationRepo.findByUserId(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Locacion not Found"));

        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            validationAddress(request.getAddress());
            MapsDTO map = mapService.geocode(request.getAddress()); 
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

    public void validationAddress(String address) {
        if (address == null || address.isEmpty()) throw new IllegalArgumentException("Addres no puede estar vacio");
    }

    public void validationComuna(String comuna) {
        if (comuna == null || comuna.isEmpty()) throw new IllegalArgumentException("comuna no puede estar vasia");
    }

    public void validationRegion(String region) {
        if (region == null || region.isEmpty()) throw new IllegalArgumentException("region no puede estar vacio");
    }


}