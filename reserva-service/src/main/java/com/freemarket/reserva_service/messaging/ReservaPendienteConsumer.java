package com.freemarket.reserva_service.messaging;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.freemarket.reserva_service.client.AuthClient;
import com.freemarket.reserva_service.config.RabbitMQConfig;
import com.freemarket.reserva_service.enums.ReserveStatus;
import com.freemarket.reserva_service.messaging.event.ReservaPendienteEvent;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.model.Reserve;
import com.freemarket.reserva_service.model.ReserveDetails;
import com.freemarket.reserva_service.repository.ProductRepository;
import com.freemarket.reserva_service.repository.ReserveRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ReservaPendienteConsumer {

    private final AuthClient authClient;
    private final ReserveRepository reserveRepository;
    private final ProductRepository productRepository;
    private final ReservaEventPublisher eventPublisher;
    private final CircuitBreakerRegistry circuitBreakerRegistry;


    @RabbitListener(queues = RabbitMQConfig.QUEUE_PENDIENTE)
    public void procesarReservaPendiente(ReservaPendienteEvent event) {

        log.info(" Procesando reserva pendiente: {}", event.getIdReserva());

       
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("auth-service");

        if (cb.getState() == CircuitBreaker.State.OPEN) {
            log.warn(" Circuito aún OPEN. Reserva {} vuelve a la cola.", event.getIdReserva());
            throw new RuntimeException("auth-service no disponible, reintentando después");
        }

      Reserve reserve = reserveRepository.findById(event.getIdReserva())
    .orElseThrow(() -> {
        log.warn("Reserva {} aún no en BD, reintentando...", event.getIdReserva());
        return new RuntimeException("Reserva no encontrada, reintentando"); 
    });

        
        if (!reserve.getStatus().equals(ReserveStatus.PENDIENTE)) {
            log.info(" Reserva {} ya no está PENDIENTE (estado: {}). Descartando.",
                event.getIdReserva(), reserve.getStatus());
            return; 
        }

        try {
    Boolean existe = authClient.getUserById(event.getIdUser());

    if (existe == null) {
        log.warn(" auth-service sigue caído. Reserva {} vuelve a la cola.", event.getIdReserva());
        throw new RuntimeException("auth-service no disponible, reintentando después");
    }

    if (Boolean.TRUE.equals(existe)) {
        activarReserva(reserve);
        log.info(" Reserva {} activada correctamente", event.getIdReserva());
    } else {
        cancelarReservaPorUsuarioInvalido(reserve);
        log.warn("Reserva {} cancelada: usuario {} no existe",
            event.getIdReserva(), event.getIdUser());
    }

} catch (Exception e) {
    log.error(" Error al verificar usuario para reserva {}: {}",
        event.getIdReserva(), e.getMessage());
    throw e;
}
    }

    private void activarReserva(Reserve reserve) {
        for (ReserveDetails detail : reserve.getReserveDetails()) {
            Product product = detail.getProduct();

            if (product.getProductStock() < detail.getQuanty()) {
                log.warn(" Sin stock para producto {}. Cancelando reserva {}",
                    product.getIdProduct(), reserve.getIdReserva());
                cancelarReservaPorSinStock(reserve);
                return;
            }

            product.setProductStock(product.getProductStock() - detail.getQuanty());
            productRepository.save(product);
        }

        reserve.setStatus(ReserveStatus.RESERVADO);
        reserveRepository.save(reserve);
        eventPublisher.publishReservaCreated(reserve.getIdReserva(),reserve.getIdUser());
    }

    private void cancelarReservaPorUsuarioInvalido(Reserve reserve) {
        reserve.setStatus(ReserveStatus.CANCELADO);
        reserveRepository.save(reserve);
        eventPublisher.publishReservaCancelled(reserve.getIdReserva());
    }

    private void cancelarReservaPorSinStock(Reserve reserve) {
        reserve.setStatus(ReserveStatus.CANCELADO);
        reserveRepository.save(reserve);
        eventPublisher.publishReservaCancelled(reserve.getIdReserva());
    }
}