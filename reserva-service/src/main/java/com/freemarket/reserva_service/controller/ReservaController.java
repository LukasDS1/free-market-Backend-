package com.freemarket.reserva_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemarket.reserva_service.request.CancelReserveRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ReservaResponse;
import com.freemarket.reserva_service.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api-v1/reserve")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService; 

    @PostMapping("/createReserve")
    public ResponseEntity<ReservaResponse> createReserve(@RequestBody ReserveRequest request) {
            return ResponseEntity.ok().body(reservaService.createReserva(request));
      
    }

    @PatchMapping("/cancel")
    public ResponseEntity<?> cancelReserva(@RequestBody CancelReserveRequest request) {
    reservaService.deleteReserve(request);
    return ResponseEntity.ok().build();
    }

}
