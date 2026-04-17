package com.freemarket.state_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.freemarket.state_service.model.deliveryState;
import com.freemarket.state_service.model.reserveState;
import com.freemarket.state_service.model.state;
import com.freemarket.state_service.repository.deliveryStateRepository;
import com.freemarket.state_service.repository.reserveStateRepository;
import com.freemarket.state_service.repository.stateRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

public class LoadDataBase {

      @Bean
    CommandLineRunner initDatabase(stateRepository stateRepo,deliveryStateRepository DeliveryRepo,reserveStateRepository ReserveRepo){
        return args->{
            if(stateRepo.count() == 0  && DeliveryRepo.count() == 0 && ReserveRepo.count() == 0){

                state ACTIVO = new state(null,"ACTIVO","Estado Activo");
                stateRepo.save(ACTIVO);

                state INACTIVO = new state(null,"INACTIVO","Estado Inactivo");
                stateRepo.save(INACTIVO);

                deliveryState PREPARACION = new deliveryState(null,"EN PREPARACION","Estado de preparacion");
                DeliveryRepo.save(PREPARACION);

                deliveryState ENVIADO = new deliveryState(null,"ENVIADO","Enviado");
                DeliveryRepo.save(ENVIADO);

                deliveryState ENTREGADO = new deliveryState(null,"ENTREGADO","Entregado");
                DeliveryRepo.save(ENTREGADO);


                reserveState RESERVAOK = new reserveState(null, "RESERVADO", "RESERVADO");
                ReserveRepo.save(RESERVAOK);

                
                reserveState RESERVAENESPERA = new reserveState(null, "RESERVA EN ESPERA", "NO RESERVADO YET");
                ReserveRepo.save(RESERVAENESPERA);

            }else{
                System.out.println("Datos ya existen. No se cargaron.");
            }
        };
    

}


}
