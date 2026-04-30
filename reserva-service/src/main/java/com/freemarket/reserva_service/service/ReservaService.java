package com.freemarket.reserva_service.service;

import java.sql.Date;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.freemarket.reserva_service.client.AuthClient;
import com.freemarket.reserva_service.exception.ServiceUnavailableException;
import com.freemarket.reserva_service.messaging.ReservaEventPublisher;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.model.Reserve;
import com.freemarket.reserva_service.model.ReserveDetails;
import com.freemarket.reserva_service.repository.ProductRepository;
import com.freemarket.reserva_service.repository.ReserveDetailsRepository;
import com.freemarket.reserva_service.repository.ReserveRepository;
import com.freemarket.reserva_service.request.CancelReserveRequest;
import com.freemarket.reserva_service.request.ProductItemRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ReservaResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaService {

    private final AuthClient authClient;

    private final ReserveDetailsRepository reserveDetailsRepository;

    private final ReserveRepository reserveRepository;

    private final ProductRepository productRepository;

    private final ReservaEventPublisher eventPublisher;

   
    public ReservaResponse createReserva(ReserveRequest request) {

        Boolean exist = authClient.getUserById(request.getIdUser());

        if(exist == null){
            throw new ServiceUnavailableException("Service is not avalible");
        }

        if (!exist) {
            throw new IllegalArgumentException("User not Found");
        }

        Reserve reserve = new Reserve();
        reserve.setIdUser(request.getIdUser());
        reserve.setReserveDate(Date.valueOf(LocalDate.now()));
        reserve.setTotalPrice(0);
        Reserve savedReserve = reserveRepository.save(reserve);

        int total = 0;

        for (ProductItemRequest item : request.getProducts()) {

            Product product = productRepository.findById(item.getIdProduct())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (product.getProductStock() < item.getQuantity()) {
                throw new IllegalArgumentException("No hay suficientes productos");
            }
            
            product.setProductStock(product.getProductStock() - item.getQuantity());
            productRepository.save(product);
            
            ReserveDetails details = new ReserveDetails();
            details.setQuanty(item.getQuantity());
            details.setUnitPrice(product.getProductprice());
            details.setProduct(product);
            details.setReserve(savedReserve);
            reserveDetailsRepository.save(details);

            total += product.getProductprice() * item.getQuantity();
        }

        savedReserve.setTotalPrice(total);
        reserveRepository.save(savedReserve);

        eventPublisher.publishReservaCreated(savedReserve.getIdReserva());     


        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(savedReserve.getIdReserva());
        response.setReserveDate(savedReserve.getReserveDate());
        response.setTotalPrice(savedReserve.getTotalPrice());

        return response;
    }


    //cancelar reserva o devolucion lo que sea jsjs
    public void deleteReserve(CancelReserveRequest request){

        Reserve reserve = reserveRepository.findById(request.getIdReserve()).orElseThrow();
        
        if(!reserve.getIdUser().equals(request.getIdUser())){
            throw new IllegalArgumentException("La id del usuario no coincide con la id de la reserva");   
        }

        for(ReserveDetails detail : reserve.getReserveDetails() ){

        Product stockRecuperado = detail.getProduct();

        stockRecuperado.setProductStock(stockRecuperado.getProductStock() + detail.getQuanty());

        productRepository.save(stockRecuperado);
    
        }

        reserveRepository.delete(reserve);

        eventPublisher.publishReservaCancelled(request.getIdReserve()); 

        
    }

 


}
