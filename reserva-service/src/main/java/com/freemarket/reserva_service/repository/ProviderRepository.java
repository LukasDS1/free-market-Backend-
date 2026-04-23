package com.freemarket.reserva_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.reserva_service.model.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider,Long> {
    
    Optional<Provider> findByProvidername(String providername);

}
