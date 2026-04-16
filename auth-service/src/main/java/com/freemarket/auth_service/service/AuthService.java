package com.freemarket.auth_service.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.model.User;
import com.freemarket.auth_service.repository.RolRepository;
import com.freemarket.auth_service.repository.UserRepository;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.response.AuthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolRepository rolRepository;

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = userRespository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
        .token(token)
        .build();
    }

    public List<User> getAllUsers(){
       return userRespository.findAll();
    }
    
    public AuthResponse registerUser(RegisterRequest user){
        User user2 = new User();
        try {
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
           passwordEmpty(user.getPassword());
           user2.setPassword(passwordEncoder.encode(user.getPassword()));
           rolValidtationOk(user.getRol());
           user2.setRol(user.getRol());
           userRespository.save(user2);
           return AuthResponse.builder()
           .token(jwtService.getToken(user2)).build();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
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
        if(email.isEmpty() || email == null){
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
        if(username.isEmpty() || username == null){
        throw new IllegalArgumentException();
        }
    }
  

/// Validaciones de nombres (firstName , LastName)
/// 
    //Metodo para validacion de firstname vacio
    private void firstNameEmpty(String firstname){
        if(firstname.isEmpty() || firstname == null){
        throw new IllegalArgumentException();
        }
    }


    //Metodo para validacion de lastName vacio
      private void lastNameEmpty(String lastname){
        if(lastname.isEmpty() || lastname == null){
        throw new IllegalArgumentException();
        }
    }


/// Validaciones de contraseña

         //Metodo para validacion de password vacio
      private void passwordEmpty(String password){
        if(password.isEmpty() || password == null){
        throw new IllegalArgumentException();
        }
    }


//Validacionds de rol

    //metodo para validacion de Rol
    private void rolValidtationOk(Rol rol){
        rolRepository.findById(rol.getRolId()).orElseThrow(() -> new IllegalArgumentException());

    }


// Acualizacion de usuario

}
