package com.freemarket.locations_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.freemarket.locations_service.model.Location;


@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
     Optional<Location> findByUserId(Long userId);

     @Query("SELECT l FROM Location l JOIN FETCH l.comuna c JOIN FETCH c.region WHERE l.userId = :userId AND l.active = true")
     Optional<Location> findByUserIdWithComunaAndRegion(@Param("userId") Long userId);

     @Query("SELECT l FROM Location l JOIN FETCH l.comuna c JOIN FETCH c.region WHERE l.userId = :userId AND l.addressType = :addressType")
     Optional<Location> findByUserIdAndAddressType(@Param("userId") Long userId,@Param("addressType") String addressType);

     @Query("SELECT l FROM Location l JOIN FETCH l.comuna c JOIN FETCH c.region WHERE l.userId = :userId")
     List<Location> findAllByUserId(@Param("userId") Long userId);
    

     Optional<Location> findByUserIdAndActiveTrue(Long userId);



}
