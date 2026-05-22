package com.freemarket.reserva_service.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.freemarket.reserva_service.client.AuthClient;
import com.freemarket.reserva_service.enums.ReserveStatus;
import com.freemarket.reserva_service.messaging.ReservaEventPublisher;
import com.freemarket.reserva_service.messaging.ReservaPendienteProducer;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.model.Reserve;
import com.freemarket.reserva_service.model.ReserveDetails;
import com.freemarket.reserva_service.repository.ProductRepository;
import com.freemarket.reserva_service.repository.ReserveDetailsRepository;
import com.freemarket.reserva_service.repository.ReserveRepository;
import com.freemarket.reserva_service.request.CancelReserveRequest;
import com.freemarket.reserva_service.request.ProductItemRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ProductoReservaResponse;
import com.freemarket.reserva_service.response.ReservaDetalleResponse;
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

    private final ReservaPendienteProducer pendienteProducer;


    public List<ReservaResponse> getAllReservas(){
        List <Reserve> reservas =  reserveRepository.findAll();
        List <ReservaResponse> returnlist = new ArrayList<>();

        for(Reserve r : reservas){
            ReservaResponse response = new ReservaResponse();
            response.setIdReserva(r.getIdReserva());
            response.setReserveDate(r.getReserveDate());
            response.setStatus(r.getStatus().toString());
            response.setTotalPrice(r.getTotalPrice());
            returnlist.add(response);
        }
        return returnlist;
    }
    
    public ReservaDetalleResponse getReservaById(Long idReserva) {
    Reserve reserve = reserveRepository.findById(idReserva)
        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

    List<ProductoReservaResponse> products = reserve.getReserveDetails()
        .stream()
        .map(detail -> new ProductoReservaResponse(
            detail.getProduct().getIdProduct(),
            detail.getProduct().getProductname(),
            detail.getUnitPrice(),
            detail.getQuanty(),
            detail.getUnitPrice() * detail.getQuanty()
        ))
        .toList();
    return new ReservaDetalleResponse(
        reserve.getIdReserva(),
        reserve.getReserveDate(),
        reserve.getTotalPrice(),
        reserve.getStatus().name(),
        products
    );
}

   
    public ReservaResponse createReserva(ReserveRequest request,String idempotencyKey) {

        Optional<Reserve> existing = reserveRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
        Reserve r = existing.get();
        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(r.getIdReserva());
        response.setReserveDate(r.getReserveDate());
        response.setTotalPrice(r.getTotalPrice());
        response.setStatus(r.getStatus().name());
        return response;
    }

        Boolean exist = authClient.getUserById(request.getIdUser());

        if(exist == null){
            return crearReservaPendiente(request,idempotencyKey);
        }

        if (!exist) {
            throw new IllegalArgumentException("User not Found");
        }
        return crearReservaCompleta(request,ReserveStatus.RESERVADO,idempotencyKey);
    }



      private ReservaResponse crearReservaPendiente(ReserveRequest request,String idempotencyKey) {

      Optional<Reserve> existing = reserveRepository.findByIdempotencyKey(idempotencyKey);

      if (existing.isPresent()) {
        Reserve r = existing.get();
        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(r.getIdReserva());
        response.setReserveDate(r.getReserveDate());
        response.setTotalPrice(r.getTotalPrice());
        response.setStatus(r.getStatus().name());
        return response;
    }


        Reserve reserve = new Reserve();
        reserve.setIdUser(request.getIdUser());
        reserve.setReserveDate(Date.valueOf(LocalDate.now()));
        reserve.setTotalPrice(0);
        reserve.setStatus(ReserveStatus.PENDIENTE); 
        reserve.setIdempotencyKey(idempotencyKey);
        Reserve savedReserve = reserveRepository.save(reserve);
        int total = 0;

        for (ProductItemRequest item : request.getProducts()) {

            Product product = productRepository.findById(item.getIdProduct()).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
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

        pendienteProducer.enviarReservaPendiente(savedReserve.getIdReserva(), request.getIdUser());

        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(savedReserve.getIdReserva());
        response.setReserveDate(savedReserve.getReserveDate());
        response.setTotalPrice(savedReserve.getTotalPrice());
        response.setStatus("PENDIENTE"); 
        return response;
    }

      private ReservaResponse crearReservaCompleta(ReserveRequest request, ReserveStatus status, String idempotencyKey) {

         Optional<Reserve> existing = reserveRepository.findByIdempotencyKey(idempotencyKey);
         if (existing.isPresent()) {
        Reserve r = existing.get();
        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(r.getIdReserva());
        response.setReserveDate(r.getReserveDate());
        response.setTotalPrice(r.getTotalPrice());
        response.setStatus(r.getStatus().name());
        return response;
    }

        Reserve reserve = new Reserve();
        reserve.setIdUser(request.getIdUser());
        reserve.setReserveDate(Date.valueOf(LocalDate.now()));
        reserve.setTotalPrice(0);
        reserve.setStatus(status);
        reserve.setIdempotencyKey(idempotencyKey);
        Reserve savedReserve = reserveRepository.save(reserve);

        int total = 0;
        for (ProductItemRequest item : request.getProducts()) {
            Product product = productRepository.findById(item.getIdProduct())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (product.getProductStock() < item.getQuantity()) {
                throw new IllegalArgumentException("No hay suficiente stock");
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
        eventPublisher.publishReservaCreated(savedReserve.getIdReserva(),savedReserve.getIdUser());

        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(savedReserve.getIdReserva());
        response.setReserveDate(savedReserve.getReserveDate());
        response.setTotalPrice(savedReserve.getTotalPrice());
        response.setStatus("RESERVADO");
        return response;
    }



    //cancelar reserva o devolucion lo que sea jsjs
    public void deleteReserve(CancelReserveRequest request){

        Reserve reserve = reserveRepository.findById(request.getIdReserve()).orElseThrow();
    
    if(!reserve.getIdUser().equals(request.getIdUser())){
        throw new IllegalArgumentException("La id del usuario no coincide con la id de la reserva");   
    }

    if(reserve.getStatus().equals(ReserveStatus.CANCELADO)){
        throw new IllegalArgumentException("La reserva ya está cancelada");
    }

    for(ReserveDetails detail : reserve.getReserveDetails()){
        Product stockRecuperado = detail.getProduct();
        stockRecuperado.setProductStock(stockRecuperado.getProductStock() + detail.getQuanty());
        productRepository.save(stockRecuperado);
    }

    reserve.setStatus(ReserveStatus.CANCELADO);
    reserveRepository.save(reserve);

    eventPublisher.publishReservaCancelled(request.getIdReserve()); 

        
    }

    public List<ReservaDetalleResponse> getReservasByUser(Long idUser) {

    List<Reserve> reservas = reserveRepository.findByIdUser(idUser);

    return reservas.stream().map(reserve -> {

        List<ProductoReservaResponse> products = reserve.getReserveDetails()
            .stream()
            .map(detail -> new ProductoReservaResponse(
                detail.getProduct().getIdProduct(),
                detail.getProduct().getProductname(),
                detail.getUnitPrice(),
                detail.getQuanty(),
                detail.getUnitPrice() * detail.getQuanty()
            ))
            .toList();

        return new ReservaDetalleResponse(
            reserve.getIdReserva(),
            reserve.getReserveDate(),
            reserve.getTotalPrice(),
            reserve.getStatus().name(),
            products
        );

    }).toList();
}

public void cancelReservaAdmin(Long idReserva) {
    Reserve reserve = reserveRepository.findById(idReserva)
        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

    if (reserve.getStatus().equals(ReserveStatus.CANCELADO)) {
        throw new IllegalArgumentException("La reserva ya está cancelada");
    }

    for (ReserveDetails detail : reserve.getReserveDetails()) {
        Product stockRecuperado = detail.getProduct();
        stockRecuperado.setProductStock(stockRecuperado.getProductStock() + detail.getQuanty());
        productRepository.save(stockRecuperado);
    }

    reserve.setStatus(ReserveStatus.CANCELADO);
    reserveRepository.save(reserve);
    eventPublisher.publishReservaCancelled(idReserva);
}
 


}
