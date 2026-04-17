package com.freemarket.state_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.state_service.model.state;

@Repository
public interface stateRepository extends JpaRepository<state,Long>{
 
}
