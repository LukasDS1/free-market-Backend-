package com.freemarket.locations_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.freemarket.locations_service.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
     Optional<Location> findByUserId(Long userId);

}
