package com.freemarket.reserva_service.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.model.Reserve;
import com.freemarket.reserva_service.model.ReserveDetails;
import com.freemarket.reserva_service.repository.ProductRepository;
import com.freemarket.reserva_service.repository.ReserveDetailsRepository;
import com.freemarket.reserva_service.repository.ReserveRepository;
import com.freemarket.reserva_service.request.ProductItemRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ReservaResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final RestTemplate restTemplate;

    private final ReserveDetailsRepository reserveDetailsRepository;

    private final ReserveRepository reserveRepository;

    private final ProductRepository productRepository;

   
    public ReservaResponse createReserva(ReserveRequest request) {

        if (!getUserById(request.getIdUser())) {
            throw new IllegalArgumentException();
        }

        Reserve reserve = new Reserve();
        reserve.setIdUser(request.getIdUser());
        reserve.setReserveDate(System.currentTimeMillis());
        reserve.setTotalPrice(0);
        Reserve savedReserve = reserveRepository.save(reserve);

        int total = 0;

        for (ProductItemRequest item : request.getProducts()) {

            Product product = productRepository.findById(item.getIdProduct())
                .orElseThrow(() -> new IllegalArgumentException());

            if (product.getProductStock() < item.getQuantity()) {
                throw new IllegalArgumentException();
            }

            ReserveDetails details = new ReserveDetails();
            details.setQuanty(item.getQuantity());
            details.setUnitPrice(product.getProductprice());
            details.setProduct(product);
            details.setReserve(savedReserve);
            reserveDetailsRepository.save(details);
            product.setProductStock(product.getProductStock() - item.getQuantity());
            productRepository.save(product);
            total += product.getProductprice() * item.getQuantity();
        }

        savedReserve.setTotalPrice(total);
        reserveRepository.save(savedReserve);

        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(savedReserve.getIdReserva());
        response.setReserveDate(savedReserve.getReserveDate());
        response.setTotalPrice(savedReserve.getTotalPrice());

        return response;
    }

     private boolean getUserById(Long id){

        String URL = "http://auth-service/api-v1/auth/{id}";

        return restTemplate.getForObject( URL, boolean.class,id);
    }



}
