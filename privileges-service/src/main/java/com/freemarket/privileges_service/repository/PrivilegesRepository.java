package com.freemarket.privileges_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.privileges_service.model.Privileges;

@Repository
public interface PrivilegesRepository extends JpaRepository<Privileges, Long>{
    Optional<Privileges> findByPrivilegeName(String name);

}
