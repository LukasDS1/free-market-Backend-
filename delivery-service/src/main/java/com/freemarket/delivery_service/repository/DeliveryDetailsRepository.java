package com.freemarket.delivery_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.delivery_service.model.DeliveryDetails;

@Repository
public interface DeliveryDetailsRepository  extends JpaRepository<DeliveryDetails,Long>{

    Optional<DeliveryDetails> findByIdReserva(Long idReserva);
    boolean existsByIdReserva(Long idReserva);

}
