package com.freemarket.reserva_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaPendienteConsumer {
    
    private final AuthClient authClient;
    private final ReserveRepository reserveRepository;
    private final ProductRepository productRepository;
    private final ReservaEventPublisher eventPublisher;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PENDIENTE)
    public void procesarReservaPendiente(ReservaPendienteEvent event){

        log.info("Procesando Reserva Pendiente");

        //verificar estado del circuito
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("auth-service");

        if(cb.getState() == CircuitBreaker.State.OPEN){
        log.warn("Circuito aun abierto");
        throw new RuntimeException("auth-service no disponible, reintentando después");
        }

        Reserve reserve = reserveRepository.findById(event.getIdReserva()).orElseThrow( () ->  new IllegalArgumentException("Reserva no encontrada"));

        if(!reserve.getStatus().equals(ReserveStatus.PENDIENTE)){
            return;
        }

        try {
            Boolean existe = authClient.getUserById(event.getIdUser());

            if(existe){
                activarReserva(reserve);
            } else{
                cancelarReservaPorUsuarioInvalido(reserve);
            }

        } catch (Exception e) {
           log.warn("reserva cancelada, usuario no existe");
           throw e;
        }
    }

    private void activarReserva(Reserve reserve) {
        for (ReserveDetails detail : reserve.getReserveDetails()) {
            Product product = detail.getProduct();

            if (product.getProductStock() < detail.getQuanty()) {
                cancelarReservaPorSinStock(reserve);
                return;
            }

            product.setProductStock(product.getProductStock() - detail.getQuanty());
            productRepository.save(product);
        }

        reserve.setStatus(ReserveStatus.RESERVADO);
        reserveRepository.save(reserve);
        eventPublisher.publishReservaCreated(reserve.getIdReserva());
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
