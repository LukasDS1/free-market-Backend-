package com.freemarket.locations_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.locations_service.DTO.MapsDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {
    
     
    @Value("${google.maps.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;


    public MapsDTO geocode(String address) {

        String url = "https://maps.googleapis.com/maps/api/geocode/json"+ "?address=" + address+ "&key=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        return parseResponse(response);
    }


    private MapsDTO parseResponse(Map response) {

    List results = (List) response.get("results");

    if (results == null || results.isEmpty()) {
        throw new RuntimeException();
    }

    Map result = (Map) results.get(0);

    //Dirreccion 
    String formattedAddress = (String) result.get("formatted_address");
    Map geometry = (Map) result.get("geometry");
    Map location = (Map) geometry.get("location");

    double lat = ((Number) location.get("lat")).doubleValue();
    double lng = ((Number) location.get("lng")).doubleValue();

    List components = (List) result.get("address_components");

    String comuna = null;
    String region = null;

    for (Object obj : components) {

        Map component = (Map) obj;
        List types = (List) component.get("types");

        String longName = (String) component.get("long_name");

        if (types.contains("locality")) {
            comuna = longName;
        }

        if (types.contains("administrative_area_level_1")) {
            region = longName;
        }
    }

    if (comuna == null) {
        for (Object obj : components) {
            Map component = (Map) obj;
            List types = (List) component.get("types");

            if (types.contains("administrative_area_level_3")) {
                comuna = (String) component.get("long_name");
                break;
            }
        }
    }

    MapsDTO resultDto = new MapsDTO();
    resultDto.setLatitude(lat);
    resultDto.setLongitude(lng);
    resultDto.setFormattedAddress(formattedAddress);
    resultDto.setComunaNombre(comuna);
    resultDto.setRegionNombre(region);

    return resultDto;
}

}
