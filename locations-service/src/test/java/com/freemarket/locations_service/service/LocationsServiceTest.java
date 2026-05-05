package com.freemarket.locations_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
public class LocationsServiceTest {
    
    @Mock
    private LocationRepository locationRepo;
 
    @Mock
    private GoogleMapsService mapService;
 
    @Mock
    private ComunaRepository comunaRepo;
 
    @Mock
    private RegionRepository regionRepo;
 
    @Mock
    private AuthClient authClientService;
 
    @InjectMocks
    private LocationsService locationsService;
 
 
    private LocationRequest buildRequest() {
        LocationRequest req = new LocationRequest();
        req.setUserId(1L);
        req.setAddress("Av. Siempre Viva 123");
        req.setComuna("Santiago");
        req.setRegion("Metropolitana");
        return req;
    }
 
    private MapsDTO buildMapsDTO() {
        MapsDTO dto = new MapsDTO();
        dto.setFormattedAddress("Av. Siempre Viva 123, Santiago");
        dto.setLatitude(-33.45);
        dto.setLongitude(-70.65);
        dto.setComunaNombre("Santiago");
        dto.setRegionNombre("Metropolitana");
        return dto;
    }
 
    private Location buildSavedLocation(Comuna comuna) {
        Location loc = new Location();
        loc.setLocationId(1L);
        loc.setUserId(1L);
        loc.setStreetAddress("Av. Siempre Viva 123, Santiago");
        loc.setLatitude(-33.45);
        loc.setLongitud(-70.65);
        loc.setComuna(comuna);
        return loc;
    }
 
    private Comuna buildComuna(Region region) {
        Comuna comuna = new Comuna();
        comuna.setNombreComuna("Santiago");
        comuna.setRegion(region);
        return comuna;
    }
 
    private Region buildRegion() {
        Region region = new Region();
        region.setNombreRegion("Metropolitana");
        return region;
    }
 
 
    @Test
    void createUserLocation_success_returnsLocationResponse() {
        Region region = buildRegion();
        Comuna comuna = buildComuna(region);
        Location saved = buildSavedLocation(comuna);
 
        when(authClientService.getUserById(1L)).thenReturn(true);
        when(mapService.geocode(anyString())).thenReturn(buildMapsDTO());
        when(regionRepo.findByNombreRegion("Metropolitana")).thenReturn(Optional.of(region));
        when(comunaRepo.findByNombreComuna("Santiago")).thenReturn(Optional.of(comuna));
        when(locationRepo.save(any(Location.class))).thenReturn(saved);
 
        LocationResponse response = locationsService.createUserLocation(buildRequest());
 
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getStreetAddress()).isEqualTo("Av. Siempre Viva 123, Santiago");
    }
 
    @Test
    void createUserLocation_userNotFound_throwsIllegalArgument() {
        when(authClientService.getUserById(1L)).thenReturn(false);
 
        assertThatThrownBy(() -> locationsService.createUserLocation(buildRequest()))
                .isInstanceOf(IllegalArgumentException.class);
    }
 
    @Test
    void createUserLocation_serviceUnavailable_throwsServiceUnavailable() {
        when(authClientService.getUserById(1L)).thenReturn(null);
 
        assertThatThrownBy(() -> locationsService.createUserLocation(buildRequest()))
                .isInstanceOf(ServiceUnavailableException.class);
    }
 
    @Test
    void createUserLocation_addressEmpty_throwsIllegalArgument() {
        LocationRequest request = buildRequest();
        request.setAddress("");
 
        assertThatThrownBy(() -> locationsService.createUserLocation(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
 
 
    @Test
    void updateLocation_success_returnsUpdatedLocationResponse() {
        Region region = buildRegion();
        Comuna comuna = buildComuna(region);
        Location existing = buildSavedLocation(comuna);
        Location saved = buildSavedLocation(comuna);
        saved.setStreetAddress("Nueva Calle 456, Santiago");
 
        when(locationRepo.findByUserId(1L)).thenReturn(Optional.of(existing));
        when(mapService.geocode(anyString())).thenReturn(buildMapsDTO());
        when(regionRepo.findByNombreRegion(anyString())).thenReturn(Optional.of(region));
        when(comunaRepo.findByNombreComuna(anyString())).thenReturn(Optional.of(comuna));
        when(locationRepo.save(any(Location.class))).thenReturn(saved);
 
        LocationResponse response = locationsService.updateLocation(buildRequest());
 
        assertThat(response.getUserId()).isEqualTo(1L);
    }
 
    @Test
    void updateLocation_locationNotFound_throwsIllegalArgument() {
        when(locationRepo.findByUserId(1L)).thenReturn(Optional.empty());
 
        assertThatThrownBy(() -> locationsService.updateLocation(buildRequest()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
