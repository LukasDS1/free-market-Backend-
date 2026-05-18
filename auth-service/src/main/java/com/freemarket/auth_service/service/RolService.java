package com.freemarket.auth_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.model.User;
import com.freemarket.auth_service.repository.RolRepository;
import com.freemarket.auth_service.repository.UserRepository;
import com.freemarket.auth_service.request.CreateRolRequest;
import com.freemarket.auth_service.request.RolChangeRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final UserRepository userRepository;

    public Optional<Rol> findRolById(Long idRol){
        return rolRepository.findById(idRol);

    }

   public boolean existById(Long rolId){
    return rolRepository.existsById(rolId);

    }


    public Rol createRol(CreateRolRequest request) {
        if (rolRepository.existsByRolName(request.getRolName())) {
            throw new IllegalArgumentException("Rol con nombre " + request.getRolName() + " ya existe");
        }

        Rol rol = new Rol();
        rol.setRolName(request.getRolName());
        rol.setDescription(request.getRolDescription());
        return rolRepository.save(rol);
    }

    
    public void changeUserRol(RolChangeRequest request) {
        User user = userRepository.findById(request.getIdUser())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Rol rol = rolRepository.findById(request.getIdRol())
            .orElseThrow(() -> new IllegalArgumentException("Rol not found"));

        user.setRol(rol);
        userRepository.save(user);
    }

}
