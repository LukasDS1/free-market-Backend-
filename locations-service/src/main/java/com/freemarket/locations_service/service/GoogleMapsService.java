package com.freemarket.locations_service.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.locations_service.DTO.MapsDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;

@Service
public class GoogleMapsService {

    private final RestTemplate restTemplate;

    public GoogleMapsService(@Qualifier("RestTemplateNormal") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
     @CircuitBreaker(name = "mapsService", fallbackMethod = "geocodeFallback")
    @TimeLimiter(name = "mapsService")
    public CompletableFuture<MapsDTO> geocode(String address) {
        return CompletableFuture.supplyAsync(() -> {

            String url = "https://nominatim.openstreetmap.org/search"
                       + "?q=" + address
                       + "&format=json&addressdetails=1&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "locations-service/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, List.class
            );

            List results = response.getBody();

            if (results == null || results.isEmpty()) {
                throw new RuntimeException("Dirección no encontrada: " + address);
            }

            return parseResponse((Map) results.get(0));
        });
    }

    // Se ejecuta si OpenStreetMap no responde o tarda más de 5s
    public CompletableFuture<MapsDTO> geocodeFallback(String address, Exception ex) {
        CompletableFuture<MapsDTO> failed = new CompletableFuture<>();
        failed.completeExceptionally(
            new RuntimeException("Servicio de mapas no disponible para: " + address)
        );
        return failed;
    }

    private MapsDTO parseResponse(Map result) {
        double lat = Double.parseDouble((String) result.get("lat"));
        double lng = Double.parseDouble((String) result.get("lon"));
        String formattedAddress = (String) result.get("display_name");

        Map address = (Map) result.get("address");

        String comuna = (String) address.getOrDefault("city",
                         address.getOrDefault("town",
                         address.getOrDefault("village",
                         address.get("county"))));

        String region = (String) address.get("state");

        MapsDTO dto = new MapsDTO();
        dto.setLatitude(lat);
        dto.setLongitude(lng);
        dto.setFormattedAddress(formattedAddress);
        dto.setComunaNombre(comuna);
        dto.setRegionNombre(region);

        return dto;
    }

}


