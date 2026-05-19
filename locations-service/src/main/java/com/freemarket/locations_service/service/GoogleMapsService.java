package com.freemarket.locations_service.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.freemarket.locations_service.DTO.MapsDTO;
import com.freemarket.locations_service.client.ConfigClient;
import com.freemarket.locations_service.response.SystemConfigResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GoogleMapsService {

    private final RestTemplate restTemplate;
    private final ConfigClient configClient;

    public GoogleMapsService(
            @Qualifier("RestTemplateNormal") RestTemplate restTemplate,
            ConfigClient configClient) {
        this.restTemplate = restTemplate;
        this.configClient = configClient;
    }

    public MapsDTO geocode(String street, String streetNumber, String comuna, String region) {

        SystemConfigResponse config = configClient.getSearchCountry();
        String countryCode = config.getConfigValue().toLowerCase().trim();
        String countryName = config.getCountryName(); 

        String cleanStreet = street
                .replaceAll("(?i)^c\\.\\s*", "")
                .replaceAll("(?i)^av\\.\\s*", "Avenida ")
                .replaceAll("(?i)^pje\\.\\s*", "Pasaje ")
                .trim();

        MapsDTO result = tryStructured(cleanStreet, streetNumber, comuna, region, countryCode, countryName);
        if (result != null) return result;

        result = tryFreeText(cleanStreet, streetNumber, comuna, region, countryCode, countryName);
        if (result != null) return result;

        result = tryStreetOnly(cleanStreet, streetNumber, comuna, region, countryCode, countryName);
        if (result != null) return result;

        throw new IllegalArgumentException(
                "Dirección no encontrada: " + streetNumber + " " + street + ", " + comuna);
    }

    private MapsDTO tryStructured(String street, String number, String comuna,
                                   String region, String countryCode, String countryName) {
        try {
            String streetParam = encode(number + " " + street);
            String cityParam   = encode(comuna);

            String url = "https://nominatim.openstreetmap.org/search"
                    + "?street="       + streetParam
                    + "&city="         + cityParam
                    + "&countrycodes=" + countryCode
                    + "&format=json"
                    + "&addressdetails=1"
                    + "&limit=1"
                    + "&accept-language=es";

            log.info("Nominatim [structured] URL: {}", url);
            List results = exchange(url);
            log.info("Nominatim [structured] body: {}", results);

            if (results != null && !results.isEmpty())
                return parseResponse((Map) results.get(0), region, comuna, countryName, number);

        } catch (Exception e) {
            log.warn("Nominatim [structured] falló: {}", e.getMessage());
        }
        return null;
    }

    private MapsDTO tryFreeText(String street, String number, String comuna,
                                 String region, String countryCode, String countryName) {
        try {
            String query = encode(number + " " + street + ", " + comuna);

            String url = "https://nominatim.openstreetmap.org/search"
                    + "?q="            + query
                    + "&countrycodes=" + countryCode
                    + "&format=json"
                    + "&addressdetails=1"
                    + "&limit=1"
                    + "&accept-language=es";

            log.info("Nominatim [freetext] URL: {}", url);
            List results = exchange(url);
            log.info("Nominatim [freetext] body: {}", results);

            if (results != null && !results.isEmpty())
                return parseResponse((Map) results.get(0), region, comuna, countryName, number);

        } catch (Exception e) {
            log.warn("Nominatim [freetext] falló: {}", e.getMessage());
        }
        return null;
    }

    private MapsDTO tryStreetOnly(String street, String number, String comuna,
                                   String region, String countryCode, String countryName) {
        try {
            String query = encode(street + ", " + comuna);

            String url = "https://nominatim.openstreetmap.org/search"
                    + "?q="            + query
                    + "&countrycodes=" + countryCode
                    + "&format=json"
                    + "&addressdetails=1"
                    + "&limit=1"
                    + "&accept-language=es";

            log.info("Nominatim [street-only] URL: {}", url);
            List results = exchange(url);
            log.info("Nominatim [street-only] body: {}", results);

            if (results != null && !results.isEmpty())
                return parseResponse((Map) results.get(0), region, comuna, countryName, number);

        } catch (Exception e) {
            log.warn("Nominatim [street-only] falló: {}", e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private List exchange(String url) {
        ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, HttpEntity.EMPTY, List.class);
        log.info("Nominatim status: {}", response.getStatusCode());
        return response.getBody();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private MapsDTO parseResponse(Map result, String regionRequest,
                                   String comunaRequest, String countryName,
                                   String streetNumberRequest) {
        double lat = Double.parseDouble((String) result.get("lat"));
        double lng = Double.parseDouble((String) result.get("lon"));

        Map address = (Map) result.get("address");

        String road        = (String) address.getOrDefault("road", "");
        String houseNumber = (String) address.getOrDefault("house_number", "");
        String city        = (String) address.getOrDefault("city", "");

        String number = (houseNumber == null || houseNumber.isEmpty())
                ? streetNumberRequest
                : houseNumber;

        String formattedAddress = road
                + (number == null || number.isEmpty() ? "" : " " + number)
                + (comunaRequest != null && !comunaRequest.isEmpty() ? ", " + comunaRequest : "")
                + (city.isEmpty() || city.equalsIgnoreCase(comunaRequest) ? "" : ", " + city)
                + (regionRequest != null && !regionRequest.isEmpty() ? ", " + regionRequest : "")
                + ", " + countryName;

        MapsDTO dto = new MapsDTO();
        dto.setLatitude(lat);
        dto.setLongitude(lng);
        dto.setFormattedAddress(formattedAddress);
        dto.setComunaNombre(comunaRequest);
        dto.setRegionNombre(regionRequest);
        return dto;
    }

    private String encode(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }
}