package com.freemarket.auth_service.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.freemarket.auth_service.enums.UserEnums;
import com.freemarket.auth_service.exception.NotFoundException;
import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.model.User;
import com.freemarket.auth_service.repository.RolRepository;
import com.freemarket.auth_service.repository.UserRepository;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.PasswordChangeRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.request.UpdateRequest;
import com.freemarket.auth_service.response.AuthResponse;
import com.freemarket.auth_service.response.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolRepository rolRepository;
    private final EmailService emailService;

    //  Auth 

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRespository.findByUsername(request.getUsername())
            .orElseThrow(() -> new NotFoundException("User not found"));
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(String refreshToken) {
        User user = userRespository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("Refresh token invalid"));

        if (user.getRefreshTokenExpiry().isBefore(Instant.now())) {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRespository.save(user);
            throw new IllegalArgumentException("Refresh token expired");
        }

        if (user.getStatus().equals(UserEnums.INACTIVO)) {
            throw new IllegalArgumentException("User is banned");
        }

        return buildAuthResponse(user);
    }

    public void logout(String refreshToken) {
        userRespository.findByRefreshToken(refreshToken).ifPresent(user -> {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRespository.save(user);
        });
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken  = jwtService.getToken(user);
        String refreshToken = jwtService.generateRefreshToken();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(Instant.now().plusSeconds(60 * 60 * 24 * 7));
        userRespository.save(user);
        return AuthResponse.builder()
            .token(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    //  Register 

    public AuthResponse registerUser(RegisterRequest request) {
        emailEmpty(request.getEmail());
        emailExists(request.getEmail());
        userNameEmpty(request.getUsername());
        userExists(request.getUsername());
        firstNameEmpty(request.getFirstName());
        lastNameEmpty(request.getLastName());
        passwordEmpty(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGenre(request.getGenre().toUpperCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRol(getRolCompleto(request.getRol()));

        User saved = userRespository.save(user);
        User full  = userRespository.findById(saved.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));
        return buildAuthResponse(full);
    }

    private Rol getRolCompleto(Rol rol) {
        return rolRepository.findById(rol.getRolId())
            .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    //  Update 

    public void UpdateUser(UpdateRequest request, Long idUser) {
        User user = userRespository.findById(idUser)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.getEmail() != null) {
            emailEmpty(request.getEmail());
            emailExists(request.getEmail());
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null) {
            userNameEmpty(request.getUsername());
            userExists(request.getUsername());
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null) {
            passwordEmpty(request.getPassword());
            if (passwordEncoder.matches(request.getPassword(), user.getPassword()))
                throw new IllegalArgumentException("New password cannot be the same as the current one");
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getGenre() != null) {
            user.setGenre(request.getGenre().toUpperCase());
        }

        userRespository.save(user);
    }

    public void setUserState(Long idUser) {
        User user = userRespository.findById(idUser)
            .orElseThrow(() -> new NotFoundException("User not found"));

        user.setStatus(user.getStatus().equals(UserEnums.ACTIVO) ? UserEnums.INACTIVO : UserEnums.ACTIVO);
        userRespository.save(user);
    }

    //  Read / Delete 

    public List<User> getAllUsers() {
        return userRespository.findAll();
    }

    public boolean getByid(Long id) {
        return userRespository.existsById(id);
    }

    public List<UserResponse> getAllResponseUser() {
        List<UserResponse> list = new ArrayList<>();
        for (User u : userRespository.findAll()) {
            UserResponse r = new UserResponse();
            r.setId(u.getUserId());
            r.setState(u.getStatus().toString());
            r.setFirstname(u.getFirstName());
            r.setLastname(u.getLastName());
            r.setEmail(u.getEmail());
            r.setUsername(u.getUsername());
            r.setIdRol(u.getRol().getRolId());
            r.setRol(u.getRol().getRolName());
            r.setGenero(u.getGenre());
            list.add(r);
        }
        return list;
    }

    public void deleteUser(Long id) {
        userRespository.deleteById(id);
    }

    public void deleteUserByAdmin(Long id) {
        userRespository.deleteById(id);
    }

    //  Password reset 

    public void requestPasswordReset(String email) {
        User user = userRespository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Email not found"));

        String token = UUID.randomUUID().toString();
        user.setResetTokenPassword(token);
        user.setResetTokenPasswordExpiry(Instant.now().plusSeconds(900));
        userRespository.save(user);
        emailService.sendPasswordReset(email, token);
    }

    public void resetPassword(PasswordChangeRequest request) {
        User user = userRespository.findByResetTokenPassword(request.getToken())
            .orElseThrow(() -> new IllegalArgumentException("Token invalid"));

        if (user.getResetTokenPasswordExpiry().isBefore(Instant.now()))
            throw new IllegalArgumentException("Token expired");

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new IllegalArgumentException("New password cannot be the same as your current password");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetTokenPassword(null);
        user.setResetTokenPasswordExpiry(null);
        userRespository.save(user);
    }

    public void validateToken(String token) {
        User user = userRespository.findByResetTokenPassword(token)
            .orElseThrow(() -> new IllegalArgumentException("Token invalid"));

        if (user.getResetTokenPasswordExpiry().isBefore(Instant.now())) {
            user.setResetTokenPassword(null);
            user.setResetTokenPasswordExpiry(null);
            userRespository.save(user);
            throw new IllegalArgumentException("Token expired");
        }
    }

    //  Validaciones 

    private void emailExists(String email) {
        if (userRespository.existsByEmail(email))
            throw new IllegalArgumentException("Email already exists");
    }

    private void emailEmpty(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is required");
    }

    private void userExists(String username) {
        if (userRespository.existsByUsername(username))
            throw new IllegalArgumentException("Username already exists");
    }

    private void userNameEmpty(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username is required");
    }

    private void firstNameEmpty(String firstname) {
        if (firstname == null || firstname.isBlank())
            throw new IllegalArgumentException("First name is required");
    }

    private void lastNameEmpty(String lastname) {
        if (lastname == null || lastname.isBlank())
            throw new IllegalArgumentException("Last name is required");
    }

    private void passwordEmpty(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password is required");
    }
}