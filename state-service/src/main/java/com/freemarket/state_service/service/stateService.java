package com.freemarket.state_service.service;

import com.freemarket.state_service.model.state;
import com.freemarket.state_service.repository.stateRepository;
import com.jetbrains.exported.JBRApi.Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class stateService {

private final stateRepository stateRepository;


    //agregar conectividad con cliente
    private boolean isAvalible(state State){
        if(State.getStateName().equals("ACTIVO")){
            return true;
        } else {
            return false;
        }

    }



}
