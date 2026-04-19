package com.freemarket.privileges_service.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemarket.privileges_service.request.moduloRequest;
import com.freemarket.privileges_service.response.ResponseDTO;
import com.freemarket.privileges_service.response.moduloResponse;
import com.freemarket.privileges_service.service.Services;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api-v1/privileges")
@AllArgsConstructor
public class Controller {
    
    private final Services services;

    @PostMapping("/modules")
    public ResponseEntity<moduloResponse> createModulo(@RequestBody moduloRequest request){
        moduloResponse response = services.createModulo(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<List<ResponseDTO>> getPrivilegesByRole(@PathVariable Long id) {
        List<ResponseDTO> response = services.getPrivilegesByRole(id);
        return ResponseEntity.ok(response);
    }
}

