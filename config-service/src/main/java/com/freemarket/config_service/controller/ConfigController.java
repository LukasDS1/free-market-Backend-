package com.freemarket.config_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.config_service.request.ConfigRequest;
import com.freemarket.config_service.response.ConfigResponse;
import com.freemarket.config_service.service.configService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api-v1/config")
@RequiredArgsConstructor

public class ConfigController {

    private final configService configService;

    @PostMapping("/create")
    public ResponseEntity<?> createConfig(@RequestBody ConfigRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(configService.createConfiguration(request));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody ConfigRequest request){
    return ResponseEntity.ok().body(configService.updateConfiguration(id,request));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ConfigResponse> getConfigurationByIdUser(@PathVariable Long id){
    return ResponseEntity.ok().body(configService.getConfigurationByIdUser(id));
    }

}