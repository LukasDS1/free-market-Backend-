package com.freemarket.privileges_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.privileges_service.model.Modulo;
import com.freemarket.privileges_service.model.rolPrivileges;
import com.freemarket.privileges_service.repository.ModuloRepository;
import com.freemarket.privileges_service.repository.PrivilegesRepository;
import com.freemarket.privileges_service.repository.RolPrivilegesRepository;
import com.freemarket.privileges_service.request.moduloRequest;
import com.freemarket.privileges_service.response.ResponseDTO;
import com.freemarket.privileges_service.response.moduloResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class Services {

    private final ModuloRepository moduloRepository;

    private final PrivilegesRepository privilegesRepo;

    private final RestTemplate restTemplate;

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
        restTemplate.getForObject(
            "http://auth-service/api-v1/auth/role/" + roleId,Object.class
        );
    } catch (Exception e) {
        throw new IllegalArgumentException();
    }

    List<rolPrivileges> list = rolRepo.findByRoleId(roleId);

    return list.stream()
            .map(rp -> new ResponseDTO(
                    rp.getPrivilege().getPrivilegeName(),
                    rp.getPrivilege().getModulo().getModuloname()))
            .collect(Collectors.toList());
}
    

}
