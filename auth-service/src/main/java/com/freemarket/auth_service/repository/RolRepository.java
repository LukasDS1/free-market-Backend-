package com.freemarket.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.auth_service.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol,Long> {
    boolean existsById (Long rolId);
    boolean existsByRolName(String rolName);


}
