package com.freemarket.privileges_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.privileges_service.exception.GlobalExceptionHandler;
import com.freemarket.privileges_service.exception.ServiceUnavailableException;
import com.freemarket.privileges_service.request.moduloRequest;
import com.freemarket.privileges_service.response.ResponseDTO;
import com.freemarket.privileges_service.response.moduloResponse;
import com.freemarket.privileges_service.service.Services;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(Controller.class)
@Import(GlobalExceptionHandler.class)
public class ControllerTest {

    @MockitoBean
    private Services services;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    private moduloRequest buildModuloRequest() {
        moduloRequest req = new moduloRequest();
        req.setModuloname("VENTAS");
        return req;
    }

   private moduloResponse buildModuloResponse() {
    return new moduloResponse(1L, "VENTAS");
}

private List<ResponseDTO> buildResponseDTOList() {
    return List.of(new ResponseDTO("READ", "VENTAS"));
}

@Test
void createModulo_success_returnsOk() throws Exception {
    when(services.createModulo(any(moduloRequest.class))).thenReturn(buildModuloResponse());

    mockMvc.perform(post("/api-v1/privileges/modules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(buildModuloRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idModuloResponse").value(1L))
            .andExpect(jsonPath("$.moduloName").value("VENTAS")); // Jackson capitaliza: ModuloName → moduloName
}

@Test
void getPrivilegesByRole_success_returnsOk() throws Exception {
    when(services.getPrivilegesByRole(1L)).thenReturn(buildResponseDTOList());

    mockMvc.perform(get("/api-v1/privileges/role/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("READ"))
            .andExpect(jsonPath("$[0].module").value("VENTAS"));
}

    @Test
    void createModulo_duplicateName_returns400() throws Exception {
        when(services.createModulo(any(moduloRequest.class)))
                .thenThrow(new IllegalArgumentException("Modulo con nombre VENTAS ya existe"));

        mockMvc.perform(post("/api-v1/privileges/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildModuloRequest())))
                .andExpect(status().isBadRequest());
    }



    @Test
    void getPrivilegesByRole_roleNotFound_returns400() throws Exception {
        when(services.getPrivilegesByRole(1L))
                .thenThrow(new IllegalArgumentException("Rol no encontrado"));

        mockMvc.perform(get("/api-v1/privileges/role/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPrivilegesByRole_serviceUnavailable_returns503() throws Exception {
        when(services.getPrivilegesByRole(1L))
                .thenThrow(new ServiceUnavailableException("Service is not avalible"));

        mockMvc.perform(get("/api-v1/privileges/role/1"))
                .andExpect(status().isServiceUnavailable());
    }

}
