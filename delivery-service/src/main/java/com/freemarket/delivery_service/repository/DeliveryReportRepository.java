package com.freemarket.delivery_service.repository;

import com.freemarket.delivery_service.enums.ReportStatus;
import com.freemarket.delivery_service.model.DeliveryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryReportRepository extends JpaRepository<DeliveryReport, Long> {

    List<DeliveryReport> findByIdUsuario(Long idUsuario);
    List<DeliveryReport> findByStatus(ReportStatus status);
    Optional<DeliveryReport> findByIdDeliveryAndIdUsuario(Long idDelivery, Long idUsuario);
    boolean existsByIdDeliveryAndIdUsuario(Long idDelivery, Long idUsuario);
    
}