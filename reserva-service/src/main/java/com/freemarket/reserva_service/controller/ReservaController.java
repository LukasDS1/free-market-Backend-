package com.freemarket.reserva_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.reserva_service.request.CancelReserveRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ReservaResponse;
import com.freemarket.reserva_service.service.ReservaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api-v1/reserve")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService; 

    @PostMapping("/createReserve")
    public ResponseEntity<ReservaResponse> createReserve(@RequestBody ReserveRequest request) {
       try {
            return ResponseEntity.ok().body(reservaService.createReserva(request));
       } catch (Exception e) {
            return ResponseEntity.badRequest().build();
       }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteReserva(@RequestBody CancelReserveRequest request  ) {
       try {
            reservaService.deleteReserve(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
       } catch (Exception e) {
            return ResponseEntity.badRequest().build();
       }
    }
    

}
