package com.freemarket.delivery_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.delivery_service.model.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
    Optional<Delivery> findByDeliveryDetails_IdReserva(Long idReserva);


}
