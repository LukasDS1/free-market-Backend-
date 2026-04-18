package com.freemarket.state_service.service;

import org.springframework.stereotype.Service;

import com.freemarket.state_service.model.state;
import com.freemarket.state_service.repository.stateRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class stateService {

private final stateRepository stateRepository;

    public String isAvailable(Long id){

        state state = stateRepository.findById(id).orElseThrow(); 

        if(state.getStateName().equals("ACTIVO")){
            return "ACTIVO";
        }else {
            return "INACTIVO";
        }
    }



}
