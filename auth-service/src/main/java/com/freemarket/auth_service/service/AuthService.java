package com.freemarket.auth_service.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.freemarket.auth_service.enums.UserEnums;
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


    public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    User user = userRespository.findByUsername(request.getUsername()).orElseThrow();
    return buildAuthResponse(user);
}

    public List<User> getAllUsers(){
       return userRespository.findAll();
    }


    public boolean getByid(Long id){
        return userRespository.existsById(id);
    }

    public AuthResponse registerUser(RegisterRequest user){

        
        User user2 = new User();
           emailExists(user.getEmail());
           emailEmpty(user.getEmail());
           user2.setEmail(user.getEmail());
           userExists(user.getUsername());
           userNameEmpty(user.getUsername());
           user2.setUsername(user.getUsername());
           firstNameEmpty(user.getFirstName());
           user2.setFirstName(user.getFirstName());
           lastNameEmpty(user.getLastName());
           user2.setLastName(user.getLastName());
           user2.setGenre(user.getGenre().toUpperCase());
           passwordEmpty(user.getPassword());
           user2.setPassword(passwordEncoder.encode(user.getPassword()));
           user2.setRol(getRolCompleto(user.getRol()));
           User savedUser = userRespository.save(user2);

           User fullUser = userRespository.findById(savedUser.getUserId()).orElseThrow();

          return buildAuthResponse(fullUser);
    }

    private Rol getRolCompleto(Rol rol) {
    return rolRepository.findById(rol.getRolId())
        .orElseThrow(() -> new IllegalArgumentException());
}


public AuthResponse refresh(String refreshToken) {
    User user = userRespository.findByRefreshToken(refreshToken)
        .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido"));

    if (user.getRefreshTokenExpiry().isBefore(Instant.now())) {
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRespository.save(user);
        throw new IllegalArgumentException("Refresh token expirado");
    }
    
    if (user.getStatus().equals(UserEnums.INACTIVO)) {
        throw new IllegalArgumentException("Usuario bloqueado");
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
    String accessToken = jwtService.getToken(user);
    String refreshToken = jwtService.generateRefreshToken();

    user.setRefreshToken(refreshToken);
    user.setRefreshTokenExpiry(Instant.now().plusSeconds(60 * 60 * 24 * 7)); 
    userRespository.save(user);

    return AuthResponse.builder()
        .token(accessToken)
        .refreshToken(refreshToken)
        .build();
}

/// Validaciones de email
    //Metodo para validacion de email en uso
    private void emailExists(String email){
         if(userRespository.existsByEmail(email)){
            throw new IllegalArgumentException();
        }
    }

    //Metodo para validacion de email vacio 

    private void emailEmpty(String email){
        if(email == null || email.isEmpty()){
        throw new IllegalArgumentException();
        }
    }

/// Validaciones de user name


    //Metodo para validacion de usuario en uso
    private void userExists(String username){
         if(userRespository.existsByUsername(username)){
            throw new IllegalArgumentException();
        }
    }

    //Metodo para validacion de usuario vacio
    private void userNameEmpty(String username){
        if(username == null|| username.isEmpty()){
        throw new IllegalArgumentException();
        }
    }
  

/// Validaciones de nombres (firstName , LastName)
/// 
    //Metodo para validacion de firstname vacio
    private void firstNameEmpty(String firstname){
        if(firstname == null || firstname.isEmpty()){
        throw new IllegalArgumentException();
        }
    }


    //Metodo para validacion de lastName vacio
      private void lastNameEmpty(String lastname){
        if(lastname == null || lastname.isEmpty()){
        throw new IllegalArgumentException();
        }
    }


/// Validaciones de contraseña

         //Metodo para validacion de password vacio
      private void passwordEmpty(String password){
        if(password == null || password.isEmpty()){
        throw new IllegalArgumentException();
        }
    }


// Acualizacion de usuario

    public void UpdateUser(UpdateRequest user,Long idUser){
    
    User user2 = userRespository.findById(idUser).orElseThrow();
        
    if(user.getEmail() != null){
    emailEmpty(user.getEmail());
    emailExists(user.getEmail());
    user2.setEmail(user.getEmail());
    }

    if(user.getUsername() != null){
    userNameEmpty(user.getUsername());
    userExists(user.getUsername());
    user2.setUsername(user.getUsername());
    }

    if(user.getPassword() != null){
    passwordEmpty(user.getPassword());

    if(passwordEncoder.matches(user.getPassword(), user2.getPassword())){
        throw new IllegalArgumentException();
    }

    user2.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    if(user.getGenre() != null){
        user2.setGenre(user.getGenre().toUpperCase());
    }

    userRespository.save(user2);

    }

    public void setUserState(Long idUser){
        
       User exist = userRespository.findById(idUser).orElseThrow(() ->  new IllegalArgumentException("User not found"));

       if(exist.getStatus().equals(UserEnums.ACTIVO)){
       exist.setStatus(UserEnums.INACTIVO);
       } else {
       exist.setStatus(UserEnums.ACTIVO);
       }
       userRespository.save(exist);

    }

    public List<UserResponse> getAllResponseUser(){
        List<User> users = userRespository.findAll();
        List<UserResponse> returnlist = new ArrayList<>();

        for(User u : users){
            UserResponse response = new UserResponse();
            response.setId(u.getUserId());
            response.setState(u.getStatus().toString());
            response.setFirstname(u.getFirstName());
            response.setLastname(u.getLastName());
            response.setEmail(u.getEmail());
            response.setUsername(u.getUsername());
            response.setIdRol(u.getRol().getRolId());
            response.setRol(u.getRol().getRolName());
            response.setGenero(u.getGenre());
            returnlist.add(response);
        }

        return returnlist;
    }

    public void requestPasswordReset(String email) {
    User user = userRespository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("Email no encontrado"));

    String token = UUID.randomUUID().toString();
    user.setResetTokenPassword(token);
    user.setResetTokenPasswordExpiry(Instant.now().plusSeconds(900));
    userRespository.save(user);

    emailService.sendPasswordReset(email, token);

    }

public void resetPassword(PasswordChangeRequest request) {
    User user = userRespository.findByResetTokenPassword(request.getToken())
        .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

    if (user.getResetTokenPasswordExpiry().isBefore(Instant.now())) {
        throw new IllegalArgumentException("Token expirado");
    }
    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
        throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    user.setResetTokenPassword(null);
    user.setResetTokenPasswordExpiry(null);
    userRespository.save(user);
    }

    public void deleteUser(Long id){
        userRespository.deleteById(id);
    }

    public void deleteUserByAdmin(Long id){
        userRespository.deleteById(id);
    }



}
