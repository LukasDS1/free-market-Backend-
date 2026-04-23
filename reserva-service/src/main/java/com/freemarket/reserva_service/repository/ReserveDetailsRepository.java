package com.freemarket.reserva_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.reserva_service.model.ReserveDetails;

@Repository
public interface ReserveDetailsRepository extends JpaRepository<ReserveDetails,Long> {

}
