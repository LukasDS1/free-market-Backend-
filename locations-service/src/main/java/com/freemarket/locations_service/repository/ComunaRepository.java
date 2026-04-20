package com.freemarket.locations_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.locations_service.model.Comuna;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long>{

    Optional<Comuna> findByNombreComuna(String nombreComuna);
}
