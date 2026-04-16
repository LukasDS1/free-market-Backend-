package com.freemarket.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemarket.auth_service.model.User;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.response.AuthResponse;
import com.freemarket.auth_service.service.AuthService;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("api-v1/auth")

public class AuthController {

private final AuthService authService;

@PostMapping("/register")
public ResponseEntity<?> createUser(@RequestBody RegisterRequest user) {
    try {
        AuthResponse response = authService.registerUser(user);
        return ResponseEntity.ok(response); 
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}


 @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response); 
    } catch (Exception e) {
        return ResponseEntity .status(HttpStatus.UNAUTHORIZED).build();
    }
}

}
