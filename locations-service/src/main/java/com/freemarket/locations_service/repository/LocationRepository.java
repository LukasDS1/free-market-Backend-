package com.freemarket.locations_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.freemarket.locations_service.model.Location;

import feign.Param;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
     Optional<Location> findByUserId(Long userId);

     @Query("SELECT l FROM Location l JOIN FETCH l.comuna c JOIN FETCH c.region WHERE l.userId = :userId")
     Optional<Location> findByUserIdWithComunaAndRegion(@Param("userId") Long userId);


}
