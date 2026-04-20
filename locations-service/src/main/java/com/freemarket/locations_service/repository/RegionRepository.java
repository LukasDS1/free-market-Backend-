package com.freemarket.locations_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.locations_service.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long>{

   Optional<Region> findByNombreRegion(String nombreRegion);

}
