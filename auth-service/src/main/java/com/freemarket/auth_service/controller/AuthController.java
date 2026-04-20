package com.freemarket.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.request.UpdateRequest;
import com.freemarket.auth_service.response.AuthResponse;
import com.freemarket.auth_service.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import com.freemarket.auth_service.service.RolService;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@AllArgsConstructor
@RequestMapping("api-v1/auth")

public class AuthController {

private final AuthService authService;

private final RolService rolservice;


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


@PatchMapping("/update/{id}")
public ResponseEntity<?> updateUser(@PathVariable Long id,@RequestBody UpdateRequest user){
    try {
        authService.UpdateUser(id,user);
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}

@GetMapping("/state/{id}")
public ResponseEntity<String> getUserState(@PathVariable Long id) {
    try {
        String result = authService.GetState(id);
        return ResponseEntity.ok().body(result);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

@GetMapping("/role/{id}")
public ResponseEntity<?> GetRolID(@PathVariable Long id) {
    try {
        return ResponseEntity.ok().body(rolservice.findRolById(id));
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    
    }
}

@GetMapping("/{id}")
public ResponseEntity<?> getById(@PathVariable Long id) {
     try {
        return ResponseEntity.ok().body(authService.getByid(id));
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    
    }
}






}
