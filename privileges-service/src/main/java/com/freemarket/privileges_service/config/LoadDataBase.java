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

                Modulo ModuloConfig = new Modulo(null, "CONFIG_MODULO", new ArrayList<>());
                moduloRepository.save(ModuloConfig);

                Modulo ModuloDelivery = new Modulo(null,"DELIVERY_MODULO",new ArrayList<>());
                moduloRepository.save(ModuloDelivery);

                Modulo ModuloLocation = new Modulo(null, "LOCATION_MODULO", new ArrayList<>());
                moduloRepository.save(ModuloLocation);

                Privileges ReadSystemConfig = new Privileges(null, "READ_SYSTEM_CONFIG", ModuloConfig);
                privilegesRepository.save(ReadSystemConfig);

                /////////////////
                
                Privileges ReadDelivery = new Privileges(null, "READ_DELIVERY", ModuloDelivery);
                privilegesRepository.save(ReadDelivery);

                Privileges PathDelivery = new Privileges(null, "PATCH_DELIVERY", ModuloDelivery);
                privilegesRepository.save(PathDelivery);

                Privileges ReadLocation = new Privileges(null, "READ_LOCATION", ModuloLocation); // necesitas crear ModuloLocation arriba
                privilegesRepository.save(ReadLocation);


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

                Privileges ReadRol = new Privileges(null,"READ_ROL",ModuloUser);
                privilegesRepository.save(ReadRol);

                Privileges CreateLocation = new Privileges(null, "CREATE_LOCATION", ModuloLocation);
                Privileges UpdateLocation = new Privileges(null, "UPDATE_LOCATION", ModuloLocation);
                privilegesRepository.save(CreateLocation);
                privilegesRepository.save(UpdateLocation);


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

                Privileges UpdateSystemConfig = new Privileges(null, "UPDATE_SYSTEM_CONFIG", ModuloConfig);
                privilegesRepository.save(UpdateSystemConfig);   
                
                Privileges CreateSystemConfig = new Privileges(null, "CREATE_SYSTEM_CONFIG", ModuloConfig);
                privilegesRepository.save(CreateSystemConfig);   
                             

                
                Privileges UpdateDelivery  = new Privileges(null,"UPDATE_DELIVERY_STATE",ModuloDelivery);
                privilegesRepository.save(UpdateDelivery);                

                ArrayList<rolPrivileges> rolePrivilegesList = new ArrayList<>();

                

                rolePrivilegesList.add(new rolPrivileges(null, 1L, CreateUser));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, ReadUser));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, SetStateUser));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, CreateRol));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, ChangeRolUser));
                rolePrivilegesList.add(new rolPrivileges(null,1L,UpdateUser));
                rolePrivilegesList.add(new rolPrivileges(null,2L,UpdateUser));
                rolePrivilegesList.add(new rolPrivileges(null,3L,UpdateUser));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, ReadUser));
                rolePrivilegesList.add(new rolPrivileges(null, 3L, ReadUser));




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


                rolePrivilegesList.add(new rolPrivileges(null, 1L, UpdateSystemConfig));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, CreateSystemConfig));



                rolePrivilegesList.add(new rolPrivileges(null, 1L, ReadSystemConfig));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, DeleteUser));
                rolePrivilegesList.add(new rolPrivileges(null, 3L, UpdateDelivery));

                rolePrivilegesList.add(new rolPrivileges(null, 2L, PathDelivery));

                rolePrivilegesList.add(new rolPrivileges(null, 3L, PathDelivery));

                rolePrivilegesList.add(new rolPrivileges(null, 1L, ReadDelivery));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, ReadDelivery));
                rolePrivilegesList.add(new rolPrivileges(null, 3L, ReadDelivery));
                rolePrivilegesList.add(new rolPrivileges(null, 3L, ReadProduct));

                rolePrivilegesList.add(new rolPrivileges(null, 1L, ReadLocation));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, ReadLocation));
                rolePrivilegesList.add(new rolPrivileges(null, 3L, ReadLocation));

                rolePrivilegesList.add(new rolPrivileges(null, 1L, CreateLocation));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, UpdateLocation));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, CreateLocation));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, UpdateLocation));
                rolePrivilegesList.add(new rolPrivileges(null, 2L, UpdateReserve));
                rolePrivilegesList.add(new rolPrivileges(null, 3L, ReadReserve));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, DeleteUser));
                rolePrivilegesList.add(new rolPrivileges(null, 3L, DeleteUser));
                rolePrivilegesList.add(new rolPrivileges(null, 1L, ReadRol));

                rolPrivilegesRepository.saveAll(rolePrivilegesList);

            }else{
                System.out.println("Datos ya existen. No se cargaron.");
            }
        };
    

}

}
