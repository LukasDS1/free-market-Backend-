package com.freemarket.privileges_service.config;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.freemarket.privileges_service.model.Modulo;
import com.freemarket.privileges_service.model.Privileges;
import com.freemarket.privileges_service.model.rolPrivileges;
import com.freemarket.privileges_service.repository.ModuloRepository;
import com.freemarket.privileges_service.repository.PrivilegesRepository;
import com.freemarket.privileges_service.repository.RolPrivilegesRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

public class LoadDataBase {
    
      @Bean
    CommandLineRunner initDatabase(ModuloRepository moduloRepository,PrivilegesRepository privilegesRepository,RolPrivilegesRepository rolPrivilegesRepository){
        return args->{
            if(moduloRepository.count() == 0  && privilegesRepository.count() == 0 && rolPrivilegesRepository.count() == 0){

                Modulo ModuloUser = new Modulo(null, "USER_MODULO",new ArrayList<>());
                moduloRepository.save(ModuloUser);

                Modulo ModuloProducto = new Modulo(null, "PRODUCTO_MODULO",new ArrayList<>());
                moduloRepository.save(ModuloProducto);

                Modulo ModuloReserve = new Modulo(null, "RESERVE_MODULO",new ArrayList<>());
                moduloRepository.save(ModuloReserve);

                /////////////////

                Privileges CreateUser  = new Privileges(null, "CREATE_USER", ModuloUser);
                privilegesRepository.save(CreateUser);

                Privileges ReadUser  = new Privileges(null, "READ_USER", ModuloUser);
                privilegesRepository.save(ReadUser);

                Privileges UpdateUser  = new Privileges(null, "UPDATE_USER", ModuloUser);
                privilegesRepository.save(UpdateUser);

                Privileges DeleteUser  = new Privileges(null, "DELETE_USER", ModuloUser);
                privilegesRepository.save(DeleteUser);

                Privileges CreateRol     = new Privileges(null, "CREATE_ROL",      ModuloUser);
                privilegesRepository.save(CreateRol);

                Privileges ChangeRolUser = new Privileges(null, "CHANGE_ROL_USER", ModuloUser);
                privilegesRepository.save(ChangeRolUser);


                /////////////////

                Privileges CreateProduct  = new Privileges(null, "CREATE_PRODUCT", ModuloProducto);
                privilegesRepository.save(CreateProduct);

                Privileges ReadProduct  = new Privileges(null, "READ_PRODUCT", ModuloProducto);
                privilegesRepository.save(ReadProduct);

                Privileges UpdateProduct  = new Privileges(null, "UPDATE_PRODUCT", ModuloProducto);
                privilegesRepository.save(UpdateProduct);

                Privileges DeleteProduct  = new Privileges(null, "DELETE_PRODUCT", ModuloProducto);
                privilegesRepository.save(DeleteProduct);

                Privileges SetStateUser = new Privileges(null, "SET_STATE_USER", ModuloUser);
                privilegesRepository.save(SetStateUser);

                /////////////////
                 
                Privileges CreateReserve  = new Privileges(null, "CREATE_RESERVE", ModuloReserve);
                privilegesRepository.save(CreateReserve);

                Privileges UpdateReserve  = new Privileges(null, "UPDATE_RESERVE", ModuloReserve);
                privilegesRepository.save(UpdateReserve);

                Privileges ReadReserve  = new Privileges(null, "READ_RESERVE", ModuloReserve);
                privilegesRepository.save(ReadReserve);

                Privileges DeleteReserve  = new Privileges(null, "DELETE_RESERVE", ModuloReserve);
                privilegesRepository.save(DeleteReserve);

                ///////
                
                ArrayList<rolPrivileges> rolePrivilegesList = new ArrayList<>();

                rolePrivilegesList.add(new rolPrivileges(null, 1L, CreateUser));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, ReadUser));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, DeleteUser));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, SetStateUser));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, CreateRol));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, ChangeRolUser));

                rolePrivilegesList.add(new rolPrivileges(null, 1L, CreateProduct));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, ReadProduct));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, UpdateProduct));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, DeleteProduct));

                rolePrivilegesList.add(new rolPrivileges(null, 1L, CreateReserve));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, ReadReserve));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, UpdateReserve));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, DeleteReserve));

                rolePrivilegesList.add(new rolPrivileges(null, 2L, ReadProduct));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, CreateReserve));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, ReadReserve));

                rolPrivilegesRepository.saveAll(rolePrivilegesList);

            }else{
                System.out.println("Datos ya existen. No se cargaron.");
            }
        };
    

}

}
