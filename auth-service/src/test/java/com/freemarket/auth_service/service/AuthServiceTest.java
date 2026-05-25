package com.freemarket.auth_service.service;

import com.freemarket.auth_service.enums.UserEnums;
import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.model.User;
import com.freemarket.auth_service.repository.RolRepository;
import com.freemarket.auth_service.repository.UserRepository;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
 
import java.time.Instant;
import java.util.List;
import java.util.Optional;
 
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    
    @Mock
    private UserRepository userRepository;
 
    @Mock
    private PasswordEncoder passwordEncoder;
 
    @Mock
    private JwtService jwtService;
 
    @Mock
    private AuthenticationManager authenticationManager;
 
    @Mock
    private RolRepository rolRepository;
 
    @InjectMocks
    private AuthService authService;
 
 
    private User buildUser() {
        Rol rol = new Rol();
        rol.setRolId(1L);
 
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setGenre("M");
        user.setRol(rol);
        user.setStatus(UserEnums.ACTIVO);
        return user;
    }
 
    private RegisterRequest buildRegisterRequest() {
        Rol rol = new Rol();
        rol.setRolId(1L);
 
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setEmail("new@test.com");
        req.setFirstName("Jane");
        req.setLastName("Doe");
        req.setGenre("f");
        req.setPassword("password123");
        req.setRol(rol);
        return req;
    }
 
 
    @Test
    void login_success_returnsAuthResponse() {
        User user = buildUser();
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");
 
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.getToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken()).thenReturn("refresh-token");
        when(userRepository.save(any(User.class))).thenReturn(user);
 
        AuthResponse response = authService.login(request);
 
        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
 
    @Test
    void login_userNotFound_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("noexiste");
        request.setPassword("password");
 
        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());
 
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(Exception.class);
    }
 
 
    @Test
    void registerUser_success_returnsAuthResponse() {
        User user = buildUser();
        RegisterRequest request = buildRegisterRequest();
 
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(rolRepository.findById(1L)).thenReturn(Optional.of(user.getRol()));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(jwtService.getToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken()).thenReturn("refresh-token");
 
        AuthResponse response = authService.registerUser(request);
 
        assertThat(response.getToken()).isEqualTo("access-token");
    }
 
    @Test
    void registerUser_emailAlreadyExists_throwsException() {
        RegisterRequest request = buildRegisterRequest();
        when(userRepository.existsByEmail("new@test.com")).thenReturn(true);
 
        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
 
   @Test
    void registerUser_emailEmpty_throwsException() {
    RegisterRequest request = buildRegisterRequest();
    request.setEmail("");

    assertThatThrownBy(() -> authService.registerUser(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email is required");
}
 
    @Test
    void registerUser_usernameAlreadyExists_throwsException() {
        RegisterRequest request = buildRegisterRequest();
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(true);
 
        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
 
    @Test
    void registerUser_passwordEmpty_throwsException() {
        RegisterRequest request = buildRegisterRequest();
        request.setPassword("");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
 
        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
 
    @Test
    void logout_clearsRefreshToken() {
        User user = buildUser();
        user.setRefreshToken("some-token");
        user.setRefreshTokenExpiry(Instant.now().plusSeconds(3600));
 
        when(userRepository.findByRefreshToken("some-token")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
 
        authService.logout("some-token");
 
        verify(userRepository).save(argThat(u -> u.getRefreshToken() == null));
    }
 
    @Test
    void logout_tokenNotFound_noExceptionThrown() {
        when(userRepository.findByRefreshToken("ghost-token")).thenReturn(Optional.empty());
 
        authService.logout("ghost-token");
 
        verify(userRepository, never()).save(any());
    }
 
 
    @Test
    void getAllUsers_returnsListFromRepository() {
        User user = buildUser();
        when(userRepository.findAll()).thenReturn(List.of(user));
 
        List<User> result = authService.getAllUsers();
 
        assertThat(result).hasSize(1).contains(user);
    }
 
    @Test
    void getByid_returnsTrue_whenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        assertThat(authService.getByid(1L)).isTrue();
    }
 
    @Test
    void getByid_returnsFalse_whenUserNotExists() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThat(authService.getByid(99L)).isFalse();
    }
 

}
