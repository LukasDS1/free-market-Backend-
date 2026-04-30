package com.freemarket.privileges_service.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.freemarket.privileges_service.client.Client;
import com.freemarket.privileges_service.model.Modulo;
import com.freemarket.privileges_service.model.rolPrivileges;
import com.freemarket.privileges_service.repository.ModuloRepository;
import com.freemarket.privileges_service.repository.RolPrivilegesRepository;
import com.freemarket.privileges_service.request.moduloRequest;
import com.freemarket.privileges_service.response.ResponseDTO;
import com.freemarket.privileges_service.response.moduloResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class Services {

    private final ModuloRepository moduloRepository;

    private final Client rest;

    private final RolPrivilegesRepository rolRepo;

    
    public moduloResponse createModulo(moduloRequest request) {

        if (moduloRepository.findByModuloname(request.getModuloname())) {
        throw new IllegalArgumentException();
    }

    Modulo modulo = new Modulo();
    modulo.setModuloname(request.getModuloname());
    
    Modulo moduloGuardado = moduloRepository.save(modulo);

    return new moduloResponse(moduloGuardado.getModuloId(),moduloGuardado.getModuloname());
    }



public List<ResponseDTO> getPrivilegesByRole(Long roleId) {

    try {
        rest.getRoleById(roleId);
    } catch (Exception ex) {
        throw new IllegalArgumentException("Service is not avalible");
    }

    List<rolPrivileges> list = rolRepo.findByRoleId(roleId);

    return list.stream()
        .map(rp -> new ResponseDTO(
            rp.getPrivilege().getPrivilegeName(),
            rp.getPrivilege().getModulo().getModuloname()))
        .collect(Collectors.toList());
}
    

}
