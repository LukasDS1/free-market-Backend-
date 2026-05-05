package com.freemarket.privileges_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.privileges_service.client.Client;
import com.freemarket.privileges_service.exception.ServiceUnavailableException;
import com.freemarket.privileges_service.model.Modulo;
import com.freemarket.privileges_service.model.Privileges;
import com.freemarket.privileges_service.model.rolPrivileges;
import com.freemarket.privileges_service.repository.ModuloRepository;
import com.freemarket.privileges_service.repository.RolPrivilegesRepository;
import com.freemarket.privileges_service.request.moduloRequest;
import com.freemarket.privileges_service.response.ResponseDTO;
import com.freemarket.privileges_service.response.moduloResponse;

@ExtendWith(MockitoExtension.class)
public class ServicesTest {

    @Mock
    private ModuloRepository moduloRepository;

    @Mock
    private Client rest;

    @Mock
    private RolPrivilegesRepository rolRepo;

    @InjectMocks
    private Services services;



    private moduloRequest buildModuloRequest() {
        moduloRequest req = new moduloRequest();
        req.setModuloname("VENTAS");
        return req;
    }

    private Modulo buildModulo() {
        Modulo modulo = new Modulo();
        modulo.setModuloId(1L);
        modulo.setModuloname("VENTAS");
        return modulo;
    }

    private rolPrivileges buildRolPrivilege() {
    Modulo modulo = buildModulo();

    Privileges privilege = new Privileges();
    privilege.setPrivilegeName("READ");
    privilege.setModulo(modulo);

    rolPrivileges rp = new rolPrivileges();
    rp.setPrivilege(privilege);
    return rp;
}
    



    @Test
    void createModulo_success_returnsModuloResponse() {
        Modulo saved = buildModulo();

        when(moduloRepository.findByModuloname("VENTAS")).thenReturn(false);
        when(moduloRepository.save(any(Modulo.class))).thenReturn(saved);

        moduloResponse response = services.createModulo(buildModuloRequest());

        assertThat(response.getIdModuloResponse()).isEqualTo(1L);
        assertThat(response.getModuloName()).isEqualTo("VENTAS");
    }

    @Test
    void createModulo_duplicateName_throwsIllegalArgument() {
        when(moduloRepository.findByModuloname("VENTAS")).thenReturn(true);

        assertThatThrownBy(() -> services.createModulo(buildModuloRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("VENTAS");
    }



    @Test
    void getPrivilegesByRole_success_returnsResponseDTOList() {
        when(rest.getRoleById(1L)).thenReturn(true);
        when(rolRepo.findByRoleId(1L)).thenReturn(List.of(buildRolPrivilege()));

        List<ResponseDTO> result = services.getPrivilegesByRole(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("READ");
        assertThat(result.get(0).getModule()).isEqualTo("VENTAS");
    }

    @Test
    void getPrivilegesByRole_roleNotFound_throwsIllegalArgument() {
        when(rest.getRoleById(1L)).thenReturn(false);

        assertThatThrownBy(() -> services.getPrivilegesByRole(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getPrivilegesByRole_serviceUnavailable_throwsServiceUnavailable() {
        when(rest.getRoleById(1L)).thenReturn(null);

        assertThatThrownBy(() -> services.getPrivilegesByRole(1L))
                .isInstanceOf(ServiceUnavailableException.class);
    }

    @Test
    void getPrivilegesByRole_noPrivileges_returnsEmptyList() {
        when(rest.getRoleById(1L)).thenReturn(true);
        when(rolRepo.findByRoleId(1L)).thenReturn(List.of());

        List<ResponseDTO> result = services.getPrivilegesByRole(1L);

        assertThat(result).isEmpty();
    }

}
