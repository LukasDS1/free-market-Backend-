package com.freemarket.auth_service.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.model.User;
import com.freemarket.auth_service.repository.RolRepository;
import com.freemarket.auth_service.repository.UserRepository;
import com.freemarket.auth_service.request.LoginRequest;
import com.freemarket.auth_service.request.RegisterRequest;
import com.freemarket.auth_service.request.UpdateRequest;
import com.freemarket.auth_service.response.AuthResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRespository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolRepository rolRepository;
    private final RestTemplate restTemplate;


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


    public boolean getByid(Long id){
        return userRespository.existsById(id);
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
           user2.setGenre(user.getGenre().toUpperCase());
           passwordEmpty(user.getPassword());
           user2.setPassword(passwordEncoder.encode(user.getPassword()));
           user2.setRol(getRolCompleto(user.getRol()));
           user2.setStateId(1L);

           User savedUser = userRespository.save(user2);

           User fullUser = userRespository.findById(savedUser.getUserId()).orElseThrow();

           return AuthResponse.builder()
           .token(jwtService.getToken(fullUser)).build();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
    }

    private Rol getRolCompleto(Rol rol) {
    return rolRepository.findById(rol.getRolId())
        .orElseThrow(() -> new IllegalArgumentException());
}

//Conexion con rest
//implementacion de TIME OUT + CIRCUIT BREAKER


@CircuitBreaker(name = "auhtService",fallbackMethod = "getStateFallback")
@TimeLimiter(name = "auhtService" )
public CompletableFuture<String> GetState(Long userId){

        User user = userRespository.findById(userId).orElseThrow();

        String URL = "http://state-service/api-v1/state/{id}";
        

        return CompletableFuture.supplyAsync(() -> restTemplate.getForObject(URL, String.class,user.getUserId()));
    }



//fallback pa cuando se abra el hilo
public CompletableFuture<String> getStateFallback(Long userId, Exception ex){
    return CompletableFuture.completedFuture("State is not avalible ");
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

    public void UpdateUser(Long id,UpdateRequest user){
    
    User user2 = userRespository.findById(id).orElseThrow();
        
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

}
