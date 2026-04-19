package com.freemarket.privileges_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.privileges_service.model.Modulo;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long>{

    boolean findByModuloname(String moduloname);

    boolean findByModuloId(Long moduloId);

    

}
