package com.freemarket.auth_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.request.*;
import com.freemarket.auth_service.response.AuthResponse;
import com.freemarket.auth_service.response.UserResponse;
import com.freemarket.auth_service.response.rolResponse;
import com.freemarket.auth_service.service.AuthService;
import com.freemarket.auth_service.service.RolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("api-v1/auth")
@Tag(name = "Authentication Controller", description = "Endpoints for authentication, registration, update and user management")
public class AuthController {

    private final AuthService authService;
    private final RolService rolService;

    // ── Password reset 

    @PostMapping("/password/validate-token")
    @Operation(summary = "Validate reset token", description = "Checks if the password reset token is valid and not expired")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token is valid"),
        @ApiResponse(responseCode = "400", description = "Token invalid or expired",
            content = @Content(examples = @ExampleObject(value = "Token invalid")))
    })
    public ResponseEntity<Void> validateToken(@RequestBody Map<String, String> body) {
        authService.validateToken(body.get("token"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset-request")
    @Operation(summary = "Request password reset", description = "Sends a reset token to the user's email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token sent successfully"),
        @ApiResponse(responseCode = "404", description = "Email not found",
            content = @Content(examples = @ExampleObject(value = "Email not found")))
    })
    public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password", description = "Changes the user password using the token received by email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Token invalid, expired, or same password",
            content = @Content(examples = @ExampleObject(value = "Token invalid"))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(examples = @ExampleObject(value = "User not found")))
    })
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordChangeRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    // ── Auth 

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns JWT + Refresh Token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(examples = @ExampleObject(value = "Invalid credentials"))),
        @ApiResponse(responseCode = "503", description = "Service unavailable",
            content = @Content(examples = @ExampleObject(value = "Service is not available yet, try again later")))
    })
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Generates a new access token using the refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "400", description = "Refresh token invalid or expired",
            content = @Content(examples = @ExampleObject(value = "Refresh token invalid"))),
        @ApiResponse(responseCode = "503", description = "Service unavailable",
            content = @Content(examples = @ExampleObject(value = "Service is not available yet, try again later")))
    })
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refresh(body.get("refreshToken")));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates the user's refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "400", description = "Refresh token invalid",
            content = @Content(examples = @ExampleObject(value = "Refresh token invalid")))
    })
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        authService.logout(body.get("refreshToken"));
        return ResponseEntity.ok().build();
    }

    //  Users 

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Creates a new user and returns JWT + Refresh Token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data",
            content = @Content(examples = {
                @ExampleObject(name = "Duplicate username", value = "Username already exists"),
                @ExampleObject(name = "Duplicate email",   value = "Email already exists")
            })),
        @ApiResponse(responseCode = "503", description = "Service unavailable",
            content = @Content(examples = @ExampleObject(value = "Service is not available yet, try again later")))
    })
    public ResponseEntity<AuthResponse> createUser(@RequestBody RegisterRequest user) {
        return ResponseEntity.ok(authService.registerUser(user));
    }

    @PatchMapping("/update")
    @Operation(summary = "Update user", description = "Updates specific fields of the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Update successful"),
        @ApiResponse(responseCode = "400", description = "Invalid data",
            content = @Content(examples = {
                @ExampleObject(name = "Same password", value = "New password cannot be the same as the current one"),
                @ExampleObject(name = "Duplicate email", value = "Email already exists")
            })),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(examples = @ExampleObject(value = "User not found")))
    })
    public ResponseEntity<Void> updateUser(@RequestBody UpdateRequest user,
                                           @RequestHeader("X-User-Id") Long idUser) {
        authService.UpdateUser(user, idUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/setState/{id}")
    @Operation(summary = "Toggle user state", description = "Activates or deactivates a user account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "State updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(examples = @ExampleObject(value = "User not found"))),
        @ApiResponse(responseCode = "503", description = "Service unavailable",
            content = @Content(examples = @ExampleObject(value = "Service is not available yet, try again later")))
    })
    public ResponseEntity<Void> setState(@PathVariable Long id) {
        authService.setUserState(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getall")
    @Operation(summary = "Get all users", description = "Returns a list of all registered users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllResponseUser());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Check user by ID", description = "Returns true if the user exists")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Query successful"),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(examples = @ExampleObject(value = "User not found")))
    })
    public ResponseEntity<Boolean> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getByid(id));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete user", description = "Deletes the authenticated user")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/admin/{id}")
    @Operation(summary = "Delete user (admin)", description = "Deletes any user by ID (admin only)")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    public ResponseEntity<Void> deleteByAdmin(@PathVariable Long id) {
        authService.deleteUserByAdmin(id);
        return ResponseEntity.ok().build();
    }

    //  Roles 

    @GetMapping("/rol/getall")
    @Operation(summary = "Get all roles", description = "Returns all roles in the system")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    public ResponseEntity<List<rolResponse>> getAllRoles() {
        return ResponseEntity.ok(rolService.findAll());
    }

    @PostMapping("/rol")
    @Operation(summary = "Create role", description = "Creates a new role in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Role already exists",
            content = @Content(examples = @ExampleObject(value = "A role with the name 'ADMIN' already exists")))
    })
    public ResponseEntity<Rol> createRol(@RequestBody CreateRolRequest request) {
        return ResponseEntity.ok(rolService.createRol(request));
    }

    @PatchMapping("/rol/change")
    @Operation(summary = "Change user role", description = "Assigns a new role to an existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role changed successfully"),
        @ApiResponse(responseCode = "404", description = "User or role not found",
            content = @Content(examples = @ExampleObject(value = "User not found")))
    })
    public ResponseEntity<Void> changeUserRol(@RequestBody RolChangeRequest request) {
        rolService.changeUserRol(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/role/{id}")
    @Operation(summary = "Check role by ID", description = "Returns true if the role exists")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Query successful"),
        @ApiResponse(responseCode = "404", description = "Role not found",
            content = @Content(examples = @ExampleObject(value = "Role not found")))
    })
    public ResponseEntity<Boolean> getRolById(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.existById(id));
    }
}