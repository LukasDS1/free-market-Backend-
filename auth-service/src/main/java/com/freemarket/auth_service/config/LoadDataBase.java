package com.freemarket.auth_service.config;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.freemarket.auth_service.enums.UserEnums;
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

                Rol admin = new Rol(null,"ADMIN","ADMIN ROL",new ArrayList<>());
                rolRepository.save(admin);

                Rol Usuario = new Rol(null, "USER", "USER ROL",new ArrayList<>());
                rolRepository.save(Usuario);

                Rol repartidor = new Rol(null, "REPARTIDOR", "REPARTIDOR ROL",new ArrayList<>());
                rolRepository.save(repartidor);

                User ADM = new User(null,"admin@freemarket.com",passwordEncoder.encode("admin"),"admin",
                "Free","Market", "MASCULINO", UserEnums.ACTIVO,null,null,null,null,admin);
                userRepository.save(ADM);

                User USR = new User(null,"user@freemarket.com",passwordEncoder.encode("user"),"user",
                "user","Market", "MASCULINO", UserEnums.ACTIVO,null,null,null,null,Usuario);
                userRepository.save(USR);

                User RTD = new User(null,"repartidor@freemarket.com",passwordEncoder.encode("repartidor"),"repartidor",
                "repartidor","Market", "MASCULINO", UserEnums.ACTIVO,null,null,null,null,repartidor);
                userRepository.save(RTD);

            }else{
                System.out.println("Datos ya existen. No se cargaron.");
            }
        };
    

}

}
