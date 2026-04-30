package com.freemarket.config_service.service;

import java.sql.Date;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

import com.freemarket.config_service.client.AuthClient;
import com.freemarket.config_service.exception.ServiceUnavailableException;
import com.freemarket.config_service.model.Configuration;
import com.freemarket.config_service.repository.ConfigRepository;
import com.freemarket.config_service.request.ConfigRequest;
import com.freemarket.config_service.response.ConfigResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class configService {

    private final ConfigRepository configRepo;

    private final AuthClient rest;


    public ConfigResponse createConfiguration(ConfigRequest request){

        Boolean exist = rest.getUserById(request.getIdUser());

        if(exist == null){
            throw new ServiceUnavailableException("Service is not avalible");
        }

        if(!exist){
            throw new IllegalArgumentException();
        }

        if (configRepo.existsByIdUser(request.getIdUser())) {
        throw new IllegalStateException();
}

        Configuration config = new Configuration();
        config.setIdUser(request.getIdUser());

        centraliceValidation(request.getCommerceName());
        config.setCommerceName(request.getCommerceName());

        centraliceValidation(request.getLogoUrl());
        config.setLogoUrl(request.getLogoUrl());

        centraliceValidation(request.getFavicomUrl());
        config.setFavicomUrl(request.getFavicomUrl());
        
        centraliceValidation(request.getPrimaryColor());
        config.setPrimarColor(request.getPrimaryColor());

        centraliceValidation(request.getSecondaryColor());
        config.setSecondaryColor(request.getSecondaryColor());

        centraliceValidation(request.getPrincipalFont());
        config.setPrincipalfont(request.getPrincipalFont());

        config.setUpdateAt(Date.valueOf(LocalDate.now()));

        Configuration saved = configRepo.save(config);

        ConfigResponse response = new ConfigResponse();

        response.setCommerceName(saved.getCommerceName());
        response.setLogoUrl(saved.getLogoUrl());
        response.setFavicomUrl(saved.getFavicomUrl());
        response.setPrimaryColor(saved.getPrimarColor());
        response.setSecondaryColor(saved.getSecondaryColor());
        response.setPrincipalFont(saved.getPrincipalfont());
        response.setUpdateDate(saved.getUpdateAt());

        return response;

    }

    //update
    public ConfigResponse updateConfiguration(Long idUser, ConfigRequest request) {
        Boolean exist = rest.getUserById(request.getIdUser());

        if(exist == null){
            throw new ServiceUnavailableException("Service is not avalible");
        }

        if(!exist){
            throw new IllegalArgumentException();
        }

    Configuration config = configRepo.findByIdUser(idUser)
        .orElseThrow(() -> new IllegalStateException());

   
    if (request.getCommerceName() != null) {
        centraliceValidation(request.getCommerceName());
        config.setCommerceName(request.getCommerceName());
    }

    if (request.getLogoUrl() != null) {
        config.setLogoUrl(request.getLogoUrl());
    }

    if (request.getFavicomUrl() != null) {
        config.setFavicomUrl(request.getFavicomUrl());
    }

    if (request.getPrimaryColor() != null) {
        config.setPrimarColor(request.getPrimaryColor());
    }

    if (request.getSecondaryColor() != null) {
        config.setSecondaryColor(request.getSecondaryColor());
    }

    if (request.getPrincipalFont() != null) {
        centraliceValidation(request.getPrincipalFont());
        config.setPrincipalfont(request.getPrincipalFont());
    }

    config.setUpdateAt(Date.valueOf(LocalDate.now()));

    Configuration saved = configRepo.save(config);

    ConfigResponse response = new ConfigResponse();
    response.setCommerceName(saved.getCommerceName());
    response.setLogoUrl(saved.getLogoUrl());
    response.setFavicomUrl(saved.getFavicomUrl());
    response.setPrimaryColor(saved.getPrimarColor());
    response.setSecondaryColor(saved.getSecondaryColor());
    response.setPrincipalFont(saved.getPrincipalfont());
    response.setUpdateDate(saved.getUpdateAt());

    return response;
}

//obtener por id

public ConfigResponse getConfigurationByIdUser(Long idUser) {
    Configuration config = configRepo.findByIdUser(idUser).orElseThrow(() -> new IllegalStateException());
    ConfigResponse response = new ConfigResponse();
    response.setCommerceName(config.getCommerceName());
    response.setLogoUrl(config.getLogoUrl());
    response.setFavicomUrl(config.getFavicomUrl());
    response.setPrimaryColor(config.getPrimarColor());
    response.setSecondaryColor(config.getSecondaryColor());
    response.setPrincipalFont(config.getPrincipalfont());
    response.setUpdateDate(config.getUpdateAt());

    return response;
}


    //validaciones

    public void centraliceValidation(String string){
        if(string == null || string.isEmpty() ){
            throw new IllegalArgumentException();
        }
    }


    

}
