package com.freemarket.delivery_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.delivery_service.enums.DeliveryStatus;
import com.freemarket.delivery_service.model.Delivery;
import com.freemarket.delivery_service.service.DeliveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api-v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<Delivery> getDelivery(@PathVariable Long idReserva) {
        return ResponseEntity.ok(deliveryService.getReserva(idReserva));
    }


    @PatchMapping("/reserva/{idReserva}/status")
    public ResponseEntity<Delivery> updateStatus(@PathVariable Long idReserva,@RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateStatus(idReserva, status));
    }

}
