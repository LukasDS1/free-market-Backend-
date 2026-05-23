package com.freemarket.delivery_service.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.model.DeliveryDetails;
import com.freemarket.delivery_service.repository.DeliveryDetailsRepository;
import com.freemarket.delivery_service.repository.DeliveryRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoadDataBase {

    @Bean
    CommandLineRunner initData(
            DeliveryDetailsRepository detailsRepo,
            DeliveryRepository        deliveryRepo) {

        return args -> {

            if (deliveryRepo.count() > 0) return; 
            DeliveryDetails details = new DeliveryDetails();
            details.setDeliveryBeginDate(LocalDate.now());
            details.setDeliveryEndDate(LocalDate.now().plusDays(5));
            details.setIdReserva(1L);
            details.setIdUsuario(2L);
            details.setIdRepartidor(3L); 

            detailsRepo.save(details);

            Delivery delivery = new Delivery();
            delivery.setStatus(DeliveryStatus.PENDIENTE);
            delivery.setDeliveryDetails(details);

            deliveryRepo.save(delivery);

            System.out.println(" Datos iniciales de delivery_service cargados.");
        };
    }
}