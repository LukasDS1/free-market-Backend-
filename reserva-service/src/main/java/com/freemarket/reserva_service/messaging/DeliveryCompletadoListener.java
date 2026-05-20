package com.freemarket.reserva_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.freemarket.reserva_service.enums.ReserveStatus;
import com.freemarket.reserva_service.messaging.event.DeliveryCompletadoEvent;
import com.freemarket.reserva_service.repository.ReserveRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class DeliveryCompletadoListener {

    private final ReserveRepository reserveRepository;

    @RabbitListener(queues = "delivery.completado.queue")
    public void handleDeliveryCompletado(DeliveryCompletadoEvent event) {
        log.info("Delivery completado para reserva: {}", event.getIdReserva());
        reserveRepository.findById(event.getIdReserva()).ifPresent(reserve -> {
            reserve.setStatus(ReserveStatus.COMPLETO);
            reserveRepository.save(reserve);
            log.info("Reserva {} marcada como COMPLETO", event.getIdReserva());
        });
    }
}