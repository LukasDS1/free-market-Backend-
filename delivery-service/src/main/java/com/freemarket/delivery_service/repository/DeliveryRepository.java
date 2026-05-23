package com.freemarket.delivery_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.model.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
    Optional<Delivery> findByDeliveryDetails_IdReserva(Long idReserva);
    List<Delivery> findByDeliveryDetails_IdUsuario(Long idUsuario);
    List<Delivery> findByStatus(DeliveryStatus status);
    Optional<Delivery> findByDeliveryDetails_IdDeliveryDetails(Long idDeliveryDetails);
    


}
