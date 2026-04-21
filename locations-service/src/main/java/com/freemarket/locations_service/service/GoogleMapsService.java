package com.freemarket.locations_service.service;

import java.util.List;
import java.util.Map;

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

import lombok.RequiredArgsConstructor;

@Service
public class GoogleMapsService {

    private final RestTemplate restTemplate;

    public GoogleMapsService(@Qualifier("RestTemplateNormal") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MapsDTO geocode(String address) {
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
    }

    private MapsDTO parseResponse(Map result) {
        double lat = Double.parseDouble((String) result.get("lat"));
        double lng = Double.parseDouble((String) result.get("lon"));
        String formattedAddress = (String) result.get("display_name");

        Map address = (Map) result.get("address");

        // comuna: city > town > village > county
        String comuna = (String) address.getOrDefault("city",
                         address.getOrDefault("town",
                         address.getOrDefault("village",
                         address.get("county"))));

        // region
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


