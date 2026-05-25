package com.freemarket.privileges_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.freemarket.privileges_service.client.Client;
import com.freemarket.privileges_service.exception.NotFoundException;
import com.freemarket.privileges_service.exception.ServiceUnavailableException;
import com.freemarket.privileges_service.model.Modulo;
import com.freemarket.privileges_service.model.Privileges;
import com.freemarket.privileges_service.model.rolPrivileges;
import com.freemarket.privileges_service.repository.ModuloRepository;
import com.freemarket.privileges_service.repository.PrivilegesRepository;
import com.freemarket.privileges_service.repository.RolPrivilegesRepository;
import com.freemarket.privileges_service.request.AsignarPrivilegioRequest;
import com.freemarket.privileges_service.request.PrivilegeRequest;
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

    private final PrivilegesRepository privilegesRepository;

    public moduloResponse createModulo(moduloRequest request) {

        if (moduloRepository.existsByModuloname(request.getModuloname())) {
            throw new IllegalArgumentException(
                    "Module  with name " + request.getModuloname() + " alredy exist");
        }

        Modulo modulo = new Modulo();
        modulo.setModuloname(request.getModuloname());

        Modulo moduloGuardado = moduloRepository.save(modulo);

        return new moduloResponse(
                moduloGuardado.getModuloId(),
                moduloGuardado.getModuloname());
    }

    public List<ResponseDTO> getPrivilegesByRole(Long roleId) {

        Boolean exist = rest.getRoleById(roleId);

        if (exist == null) {
            throw new ServiceUnavailableException(
                    "Service is not available, try again later");
        }

        if (!exist) {
            throw new NotFoundException("Role not found");
        }

        List<rolPrivileges> list = rolRepo.findByRoleId(roleId);

        return list.stream()
                .map(rp -> new ResponseDTO(
                        rp.getPrivilege().getPrivilegeName(),
                        rp.getPrivilege().getModulo().getModuloname()))
                .collect(Collectors.toList());
    }

    // GET all modulos
    public List<moduloResponse> getAllModulos() {
        return moduloRepository.findAll()
                .stream()
                .map(m -> new moduloResponse(
                        m.getModuloId(),
                        m.getModuloname()))
                .toList();
    }

    // GET all privileges
    public List<ResponseDTO> getAllPrivileges() {
        return privilegesRepository.findAll()
                .stream()
                .map(p -> new ResponseDTO(
                        p.getPrivilegeName(),
                        p.getModulo().getModuloname()))
                .toList();
    }

    // CREATE privilege
    public ResponseDTO createPrivilege(PrivilegeRequest request) {

        if (privilegesRepository.findByPrivilegeName(
                request.getPrivilegeName()).isPresent()) {

            throw new IllegalArgumentException("Privilege already exists");
        }

        Modulo modulo = moduloRepository.findById(request.getModuloId())
                .orElseThrow(() -> new NotFoundException("Module not found"));

        Privileges p = new Privileges(
                null,
                request.getPrivilegeName(),
                modulo);

        Privileges saved = privilegesRepository.save(p);

        return new ResponseDTO(
                saved.getPrivilegeName(),
                saved.getModulo().getModuloname());
    }

    // ASIGNAR privilege a rol
    public void asignarPrivilegioARol(AsignarPrivilegioRequest request) {

        if (rolRepo.existsByRoleIdAndPrivilege_PrivilegesId(
                request.getRoleId(),
                request.getPrivilegeId())) {

            throw new IllegalArgumentException(
                    "The role already has this privilege");
        }

        Boolean rolExiste = rest.getRoleById(request.getRoleId());

        if (rolExiste == null) {
            throw new ServiceUnavailableException(
                    "Service is not available");
        }

        if (!rolExiste) {
            throw new NotFoundException("Role not found");
        }

        Privileges p = privilegesRepository.findById(
                request.getPrivilegeId())
                .orElseThrow(() ->
                        new NotFoundException("Privilege not found"));

        rolRepo.save(new rolPrivileges(
                null,
                request.getRoleId(),
                p));
    }

    // ELIMINAR privilege de rol
    public void eliminarPrivilegioDeRol(Long roleId, Long privilegeId) {

        if (!rolRepo.existsByRoleIdAndPrivilege_PrivilegesId(
                roleId,
                privilegeId)) {

            throw new IllegalArgumentException(
                    "The role does not have this privilege");
        }

        rolRepo.deleteByRoleIdAndPrivilege_PrivilegesId(
                roleId,
                privilegeId);
    }
}