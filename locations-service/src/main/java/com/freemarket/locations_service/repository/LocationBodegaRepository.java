package com.freemarket.locations_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.locations_service.model.LocationBodega;

@Repository
public interface LocationBodegaRepository extends JpaRepository<LocationBodega, Long> {

}
