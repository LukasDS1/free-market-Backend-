package com.freemarket.locations_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.locations_service.DTO.MapsDTO;
import com.freemarket.locations_service.client.AuthClient;
import com.freemarket.locations_service.excepcion.NotFoundException;
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

    @Mock private LocationRepository locationRepo;
    @Mock private GoogleMapsService mapService;
    @Mock private ComunaRepository comunaRepo;
    @Mock private RegionRepository regionRepo;
    @Mock private AuthClient authClientService;

    @InjectMocks
    private LocationsService locationsService;

    // ─── builders ─────────────────────────────────────────────────────────────

   private LocationRequest buildRequest() {
    LocationRequest req = new LocationRequest();
    req.setUserId(1L);
    req.setStreet("Apolo 11");
    req.setStreetNumber("3009");
    req.setComuna("Colina");
    req.setRegion("Metropolitana");
    req.setAddressType("HOME");
    return req;
}

    private MapsDTO buildMapsDTO() {
        MapsDTO dto = new MapsDTO();
        dto.setFormattedAddress("3009 Apolo 11, Colina, Metropolitana");
        dto.setLatitude(-33.45);
        dto.setLongitude(-70.65);
        dto.setComunaNombre("Colina");
        dto.setRegionNombre("Metropolitana");
        return dto;
    }

    private Region buildRegion() {
        Region region = new Region();
        region.setNombreRegion("Metropolitana");
        return region;
    }

    private Comuna buildComuna(Region region) {
        Comuna comuna = new Comuna();
        comuna.setNombreComuna("Colina");
        comuna.setRegion(region);
        return comuna;
    }

    private Location buildSavedLocation(Comuna comuna) {
        Location loc = new Location();
        loc.setLocationId(1L);
        loc.setUserId(1L);
        loc.setStreet("Apolo 11");
        loc.setStreetNumber("3009");
        loc.setStreetAddress("3009 Apolo 11, Colina, Metropolitana");
        loc.setLatitude(-33.45);
        loc.setLongitud(-70.65);
        loc.setComuna(comuna);
        return loc;
    }

    // ─── createUserLocation ───────────────────────────────────────────────────

@Test
void createUserLocation_success_returnsLocationResponse() {

    Region region = buildRegion();
    Comuna comuna = buildComuna(region);
    Location saved = buildSavedLocation(comuna);
    saved.setAddressType("HOME");

    when(locationRepo.findByUserIdAndAddressType(1L, "HOME"))
            .thenReturn(Optional.empty());

    when(locationRepo.findAllByUserId(1L))
            .thenReturn(Collections.emptyList());

    when(mapService.geocode(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(buildMapsDTO());

    when(regionRepo.findByNombreRegion("Metropolitana"))
            .thenReturn(Optional.of(region));

    when(comunaRepo.findByNombreComuna("Colina"))
            .thenReturn(Optional.of(comuna));

    when(locationRepo.save(any(Location.class)))
            .thenReturn(saved);

    LocationResponse response = locationsService.createUserLocation(buildRequest());

    assertThat(response.getUserId()).isEqualTo(1L);
    assertThat(response.getStreet()).isEqualTo("Apolo 11");
    assertThat(response.getStreetNumber()).isEqualTo("3009");
    assertThat(response.getStreetAddress()).isEqualTo("3009 Apolo 11, Colina, Metropolitana");
    assertThat(response.getComunaNombre()).isEqualTo("Colina");
    assertThat(response.getRegionNombre()).isEqualTo("Metropolitana");
}

@Test
void createUserLocation_streetNull_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setStreet(null);

    assertThatThrownBy(() -> locationsService.createUserLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Street cannot be empty");
}

@Test
void createUserLocation_streetEmpty_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setStreet("");

    assertThatThrownBy(() -> locationsService.createUserLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Street cannot be empty");
}

@Test
void createUserLocation_streetNumberEmpty_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setStreetNumber("");

    assertThatThrownBy(() -> locationsService.createUserLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Street number cannot be empty");
}

@Test
void createUserLocation_comunaEmpty_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setComuna("");

    assertThatThrownBy(() -> locationsService.createUserLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Comuna cannot be empty");
}

@Test
void createUserLocation_regionEmpty_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setRegion("");

    assertThatThrownBy(() -> locationsService.createUserLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Region cannot be empty");
}

@Test
void createUserLocation_addressNotFound_throwsIllegalArgument() {

    when(locationRepo.findByUserIdAndAddressType(1L, "HOME"))
            .thenReturn(Optional.empty());

    when(mapService.geocode(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Dirección no encontrada"));

    assertThatThrownBy(() -> locationsService.createUserLocation(buildRequest()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Dirección no encontrada");
}

@Test
void createUserLocation_mapsServiceUnavailable_throwsServiceUnavailable() {

    when(locationRepo.findByUserIdAndAddressType(1L, "HOME"))
            .thenReturn(Optional.empty());

    when(mapService.geocode(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new ServiceUnavailableException("Servicio de mapas no disponible"));

    assertThatThrownBy(() -> locationsService.createUserLocation(buildRequest()))
            .isInstanceOf(ServiceUnavailableException.class)
            .hasMessageContaining("Servicio de mapas no disponible");
}

@Test
void createUserLocation_regionCreatedWhenNotFound() {

    Region region = buildRegion();
    Comuna comuna = buildComuna(region);

    Location saved = buildSavedLocation(comuna);
    saved.setAddressType("HOME");

    when(locationRepo.findByUserIdAndAddressType(1L, "HOME"))
            .thenReturn(Optional.empty());

    when(locationRepo.findAllByUserId(1L))
            .thenReturn(Collections.emptyList());

    when(mapService.geocode(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(buildMapsDTO());

    when(regionRepo.findByNombreRegion("Metropolitana"))
            .thenReturn(Optional.empty());

    when(regionRepo.save(any(Region.class)))
            .thenReturn(region);

    when(comunaRepo.findByNombreComuna("Colina"))
            .thenReturn(Optional.of(comuna));

    when(locationRepo.save(any(Location.class)))
            .thenReturn(saved);

    LocationResponse response = locationsService.createUserLocation(buildRequest());

    assertThat(response.getRegionNombre()).isEqualTo("Metropolitana");
}

@Test
void updateLocation_success_returnsUpdatedLocationResponse() {

    Region region = buildRegion();
    Comuna comuna = buildComuna(region);

    Location existing = buildSavedLocation(comuna);
    existing.setAddressType("HOME");

    Location saved = buildSavedLocation(comuna);
    saved.setAddressType("HOME");

    when(locationRepo.findByUserIdAndAddressType(1L, "HOME"))
            .thenReturn(Optional.of(existing));

    when(mapService.geocode(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(buildMapsDTO());

    when(regionRepo.findByNombreRegion("Metropolitana"))
            .thenReturn(Optional.of(region));

    when(comunaRepo.findByNombreComuna("Colina"))
            .thenReturn(Optional.of(comuna));

    when(locationRepo.save(any(Location.class)))
            .thenReturn(saved);

    LocationResponse response = locationsService.updateLocation(buildRequest());

    assertThat(response.getUserId()).isEqualTo(1L);
    assertThat(response.getStreet()).isEqualTo("Apolo 11");
}

@Test
void updateLocation_streetEmpty_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setStreet("");

    assertThatThrownBy(() -> locationsService.updateLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Street cannot be empty");
}

@Test
void updateLocation_streetNumberEmpty_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setStreetNumber("");

    assertThatThrownBy(() -> locationsService.updateLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Street number cannot be empty");
}

@Test
void updateLocation_comunaEmpty_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setComuna("");

    assertThatThrownBy(() -> locationsService.updateLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Comuna cannot be empty");
}

@Test
void updateLocation_regionEmpty_throwsIllegalArgument() {
    LocationRequest request = buildRequest();
    request.setRegion("");

    assertThatThrownBy(() -> locationsService.updateLocation(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Region cannot be empty");
}

@Test
void updateLocation_locationNotFound_throwsNotFoundException() {

    when(locationRepo.findByUserIdAndAddressType(1L, "HOME"))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() -> locationsService.updateLocation(buildRequest()))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Location not found");
}

@Test
void updateLocation_addressNotFound_throwsIllegalArgument() {

    Region region = buildRegion();
    Comuna comuna = buildComuna(region);

    Location existing = buildSavedLocation(comuna);
    existing.setAddressType("HOME");

    when(locationRepo.findByUserIdAndAddressType(1L, "HOME"))
            .thenReturn(Optional.of(existing));

    when(mapService.geocode(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Dirección no encontrada"));

    assertThatThrownBy(() -> locationsService.updateLocation(buildRequest()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Dirección no encontrada");
}

@Test
void updateLocation_mapsServiceUnavailable_throwsServiceUnavailable() {

    Region region = buildRegion();
    Comuna comuna = buildComuna(region);

    Location existing = buildSavedLocation(comuna);
    existing.setAddressType("HOME");

    when(locationRepo.findByUserIdAndAddressType(1L, "HOME"))
            .thenReturn(Optional.of(existing));

    when(mapService.geocode(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new ServiceUnavailableException("Servicio de mapas no disponible"));

    assertThatThrownBy(() -> locationsService.updateLocation(buildRequest()))
            .isInstanceOf(ServiceUnavailableException.class)
            .hasMessageContaining("Servicio de mapas no disponible");
}
}