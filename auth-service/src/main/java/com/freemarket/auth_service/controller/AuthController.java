package com.freemarket.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemarket.auth_service.client.FeingClient;
import com.freemarket.auth_service.exception.ServiceUnavailableException;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.request.UpdateRequest;
import com.freemarket.auth_service.response.AuthResponse;
import com.freemarket.auth_service.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import com.freemarket.auth_service.service.RolService;


@RestController
@AllArgsConstructor
@RequestMapping("api-v1/auth")

public class AuthController {

private final AuthService authService;
    private final FeingClient feingClient;
    private final RolService rolService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createUser(@RequestBody RegisterRequest user) {
        return ResponseEntity.ok(authService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UpdateRequest user) {
        authService.UpdateUser(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/state/{id}")
    public ResponseEntity<String> getUserState(@PathVariable Long id) {
        String state = feingClient.getStateById(id);
        if (state == null) {
            throw new ServiceUnavailableException("state-service no disponible");
        }
        return ResponseEntity.ok(state);
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<?> getRolById(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.findRolById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boolean> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getByid(id));
    }

}
