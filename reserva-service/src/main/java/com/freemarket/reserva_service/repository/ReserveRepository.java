package com.freemarket.reserva_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.reserva_service.model.Reserve;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve,Long>{

        List<Reserve> findByIdUser(Long idUser);
        Optional<Reserve> findByIdempotencyKey(String idempotencyKey);





}
