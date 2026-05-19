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
import com.freemarket.locations_service.response.LocationResponseForId;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationsService {

    private final LocationRepository locationRepo;
    private final GoogleMapsService mapService;
    private final ComunaRepository comunaRepo;
    private final RegionRepository regionRepo;
    private final AuthClient authClientService;

    public LocationResponseForId getLocationByUserId(Long id) {
        Location location = locationRepo.findByUserIdWithComunaAndRegion(id).orElseThrow(
            () -> new IllegalArgumentException("Usuario no encontrado"));

        LocationResponseForId response = new LocationResponseForId();
        response.setStreetAddress(location.getStreetAddress());
        response.setComunaNombre(location.getComuna().getNombreComuna());
        response.setRegionNombre(location.getComuna().getRegion().getNombreRegion());
        return response;
    }
    public LocationResponse createUserLocation(LocationRequest request) {
    validationStreet(request.getStreet());
    validationStreetNumber(request.getStreetNumber());
    validationComuna(request.getComuna());
    validationRegion(request.getRegion());

    Boolean userExists = authClientService.getUserById(request.getUserId());
    if (userExists == null) throw new ServiceUnavailableException("Service is unavalible");
    if (!userExists) throw new IllegalArgumentException("Usuario no encontrado");

    MapsDTO map = mapService.geocode(
        request.getStreet(),
        request.getStreetNumber(),
        request.getComuna(),
        request.getRegion()
    );

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
    location.setStreet(request.getStreet());
    location.setStreetNumber(request.getStreetNumber());
    location.setStreetAddress(map.getFormattedAddress());
    location.setLatitude(map.getLatitude());
    location.setLongitud(map.getLongitude());
    location.setComuna(comuna);

    Location saved = locationRepo.save(location);

    LocationResponse response = new LocationResponse();
    response.setLocationId(saved.getLocationId());
    response.setUserId(saved.getUserId());
    response.setStreet(saved.getStreet());
    response.setStreetNumber(saved.getStreetNumber());
    response.setStreetAddress(saved.getStreetAddress());
    response.setLatitude(saved.getLatitude());
    response.setLongitude(saved.getLongitud());
    response.setComunaNombre(comuna.getNombreComuna());
    response.setRegionNombre(region.getNombreRegion());
    return response;
}
public LocationResponse updateLocation(LocationRequest request) {

    validationStreet(request.getStreet());
    validationStreetNumber(request.getStreetNumber());
    validationComuna(request.getComuna());
    validationRegion(request.getRegion());

    Location location = locationRepo.findByUserId(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Locacion not Found"));

    MapsDTO map = mapService.geocode(
            request.getStreet(),
            request.getStreetNumber(),
            request.getComuna(),
            request.getRegion()
    );

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

    location.setStreet(request.getStreet());
    location.setStreetNumber(request.getStreetNumber());
    location.setStreetAddress(map.getFormattedAddress());
    location.setLatitude(map.getLatitude());
    location.setLongitud(map.getLongitude());
    location.setComuna(comuna);

    Location saved = locationRepo.save(location);

    LocationResponse response = new LocationResponse();
    response.setLocationId(saved.getLocationId());
    response.setUserId(saved.getUserId());
    response.setStreet(saved.getStreet());
    response.setStreetNumber(saved.getStreetNumber());
    response.setStreetAddress(saved.getStreetAddress());
    response.setLatitude(saved.getLatitude());
    response.setLongitude(saved.getLongitud());
    response.setComunaNombre(comuna.getNombreComuna());
    response.setRegionNombre(region.getNombreRegion());
    return response;
}

// Validaciones
public void validationStreet(String street) {
    if (street == null || street.isEmpty()) throw new IllegalArgumentException("La calle no puede estar vacía");
}

public void validationStreetNumber(String streetNumber) {
    if (streetNumber == null || streetNumber.isEmpty()) throw new IllegalArgumentException("El número no puede estar vacío");
}

public void validationComuna(String comuna) {
    if (comuna == null || comuna.isEmpty()) throw new IllegalArgumentException("La comuna no puede estar vacía");
}

public void validationRegion(String region) {
    if (region == null || region.isEmpty()) throw new IllegalArgumentException("La región no puede estar vacía");
}
}