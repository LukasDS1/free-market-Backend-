package com.freemarket.config_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.config_service.model.Configuration;

@Repository
public interface ConfigRepository extends JpaRepository<Configuration,Long> {
     boolean existsByIdUser(Long idUser);
     Optional<Configuration> findByIdUser(Long idUser);

}
