package com.freemarket.auth_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.repository.RolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public Optional<Rol> findRolById(Long idRol){
        return rolRepository.findById(idRol);

    }

}
