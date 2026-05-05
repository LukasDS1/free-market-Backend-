package com.freemarket.state_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.freemarket.state_service.service.stateService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(StateController.class)
public class StateControllerTest {

    @MockitoBean
    private stateService stateService;
 
    @Autowired
    private MockMvc mockMvc;
 
    @Test
    void isAvailable_stateFound_returnsOkWithResult() throws Exception {
        when(stateService.isAvailable(1L)).thenReturn("ACTIVO");
 
        mockMvc.perform(get("/api-v1/state/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("ACTIVO"));
    }
 
    @Test
    void isAvailable_stateNotFound_returnsBadRequest() throws Exception {
        when(stateService.isAvailable(99L)).thenThrow(new RuntimeException());
 
        mockMvc.perform(get("/api-v1/state/99"))
                .andExpect(status().isBadRequest());
    }

}
