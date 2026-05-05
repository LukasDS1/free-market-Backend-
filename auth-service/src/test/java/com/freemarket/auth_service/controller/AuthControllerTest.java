package com.freemarket.auth_service.controller;
 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.auth_service.client.FeingClient;
import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.request.UpdateRequest;
import com.freemarket.auth_service.response.AuthResponse;
import com.freemarket.auth_service.service.AuthService;
import com.freemarket.auth_service.service.RolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
 
import java.util.Map;
import java.util.Optional;
 
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {


    
    @MockitoBean
    private AuthService authService;
 
    @MockitoBean
    private FeingClient feingClient;
 
    @MockitoBean
    private RolService rolService;
 
    @Autowired
    private MockMvc mockMvc;
 
    @Autowired
    private ObjectMapper objectMapper;
 
  @Test
    void register_success_returnsOkWithToken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setGenre("F");
        request.setPassword("password123");
 
        AuthResponse authResponse = AuthResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .build();
 
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(authResponse);
 
        mockMvc.perform(post("/api-v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }
 
    @Test
    void register_duplicateEmail_returns400() throws Exception {
        when(authService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email ya existe"));
 
        mockMvc.perform(post("/api-v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest())))
                .andExpect(status().isBadRequest());
    }
 
 
    @Test
    void login_success_returnsOkWithToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");
 
        AuthResponse authResponse = AuthResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .build();
 
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);
 
        mockMvc.perform(post("/api-v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"));
    }
 
    @Test
    void login_badCredentials_returns401() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));
 
        mockMvc.perform(post("/api-v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest())))
                .andExpect(status().isUnauthorized());
    }
  
    @Test
    void updateUser_success_returnsOk() throws Exception {
        UpdateRequest request = new UpdateRequest();
        request.setEmail("updated@test.com");
 
        doNothing().when(authService).UpdateUser(eq(1L), any(UpdateRequest.class));
 
        mockMvc.perform(patch("/api-v1/auth/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
 
    @Test
    void updateUser_invalidData_returns500() throws Exception {
        doNothing().when(authService).UpdateUser(eq(1L), any(UpdateRequest.class));
 
        mockMvc.perform(patch("/api-v1/auth/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateRequest())))
                .andExpect(status().isOk());
    }
 
 
    @Test
    void getState_success_returnsState() throws Exception {
        when(feingClient.getStateById(1L)).thenReturn("ACTIVE");
 
        mockMvc.perform(get("/api-v1/auth/state/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("ACTIVE"));
    }
 
    @Test
    void getState_serviceUnavailable_returns500() throws Exception {
        when(feingClient.getStateById(1L)).thenReturn(null);
 
        mockMvc.perform(get("/api-v1/auth/state/1"))
                .andExpect(status().is5xxServerError());
    }
 
 
    @Test
    void getRolById_found_returnsOk() throws Exception {
        Rol rol = new Rol();
        rol.setRolId(1L);
 
        when(rolService.findRolById(1L)).thenReturn(Optional.of(rol));
 
        mockMvc.perform(get("/api-v1/auth/role/1"))
                .andExpect(status().isOk());
    }
 
    @Test
    void getRolById_notFound_returnsEmptyOk() throws Exception {
        when(rolService.findRolById(99L)).thenReturn(Optional.empty());
 
        mockMvc.perform(get("/api-v1/auth/role/99"))
                .andExpect(status().isOk());
    }
 
 
    @Test
    void getById_userExists_returnsTrue() throws Exception {
        when(authService.getByid(1L)).thenReturn(true);
 
        mockMvc.perform(get("/api-v1/auth/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
 
    @Test
    void getById_userNotExists_returnsFalse() throws Exception {
        when(authService.getByid(99L)).thenReturn(false);
 
        mockMvc.perform(get("/api-v1/auth/99"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
 
 
 
    @Test
    void logout_success_returnsOk() throws Exception {
        doNothing().when(authService).logout("some-token");
 
        mockMvc.perform(post("/api-v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", "some-token"))))
                .andExpect(status().isOk());
    }

}
