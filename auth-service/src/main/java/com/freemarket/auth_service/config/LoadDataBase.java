package com.freemarket.auth_service.config;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.freemarket.auth_service.model.Rol;
import com.freemarket.auth_service.model.User;
import com.freemarket.auth_service.repository.RolRepository;
import com.freemarket.auth_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

public class LoadDataBase {

    @Bean
    CommandLineRunner initDatabase(RolRepository rolRepository, UserRepository userRepository){
        return args->{
              PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if(rolRepository.count() == 0  && userRepository.count() == 0){

                Rol admin = new Rol(null,"ADMIN","ROL ADMINISTRATIVO",new ArrayList<>());
                rolRepository.save(admin);

                Rol Usuario = new Rol(null, "USER", "Rol de usuario",new ArrayList<>());
                rolRepository.save(Usuario);

                User ADM = new User(null,"admin@gmail.com","AdminUser12#","ADMIN","Juan","Perez","MASCULINO",1L,null,null,admin);
                
                String encoded = passwordEncoder.encode(ADM.getPassword());  
                ADM.setPassword(encoded);
                userRepository.save(ADM);


                User USR = new User(null,"user@gmail.com","AdminUser12#","USER","Pablo","Pincel","MASCULINO",1L,null,null,Usuario);
                String encoded2 = passwordEncoder.encode(USR.getPassword());
                USR.setPassword(encoded2);
                userRepository.save(USR);
            }else{
                System.out.println("Datos ya existen. No se cargaron.");
            }
        };
    

}

}
