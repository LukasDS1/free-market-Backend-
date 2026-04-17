package com.freemarket.state_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemarket.state_service.service.stateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api-v1/state")
@RequiredArgsConstructor
public class StateController {

    private final stateService StateService;


    @GetMapping("/{id}")
public ResponseEntity<Boolean> isAvailable(@PathVariable Long id) {
    try {
        boolean result = StateService.isAvailable(id);
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}
    

    
    

}
