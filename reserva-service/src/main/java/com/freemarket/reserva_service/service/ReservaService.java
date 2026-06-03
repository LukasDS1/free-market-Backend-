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
import com.freemarket.reserva_service.exception.NotFoundException;
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

    public List<ReservaResponse> getAllReservas() {

        List<Reserve> reservas = reserveRepository.findAll();
        List<ReservaResponse> responseList = new ArrayList<>();

        for (Reserve reserve : reservas) {
            ReservaResponse response = new ReservaResponse();
            response.setIdReserva(reserve.getIdReserva());
            response.setReserveDate(reserve.getReserveDate());
            response.setStatus(reserve.getStatus().toString());
            response.setTotalPrice(reserve.getTotalPrice());
            responseList.add(response);
        }

        return responseList;
    }

    public ReservaDetalleResponse getReservaById(Long idReserva) {

        Reserve reserve = reserveRepository.findById(idReserva)
                .orElseThrow(() -> new NotFoundException("Reserve not found"));

        List<ProductoReservaResponse> products = reserve.getReserveDetails()
                .stream()
                .map(detail -> new ProductoReservaResponse(
                        detail.getProduct().getIdProduct(),
                        detail.getProduct().getProductname(),
                        detail.getUnitPrice(),
                        detail.getQuanty(),
                        detail.getUnitPrice() * detail.getQuanty()))
                .toList();

        return new ReservaDetalleResponse(
                reserve.getIdReserva(),
                reserve.getReserveDate(),
                reserve.getTotalPrice(),
                reserve.getStatus().name(),
                reserve.getDeliveryAddress(),
                products);
    }

    public ReservaResponse createReserva(ReserveRequest request, String idempotencyKey) {

        Optional<Reserve> existing = reserveRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            Reserve reserve = existing.get();
            ReservaResponse response = new ReservaResponse();
            response.setIdReserva(reserve.getIdReserva());
            response.setReserveDate(reserve.getReserveDate());
            response.setTotalPrice(reserve.getTotalPrice());
            response.setStatus(reserve.getStatus().name());
            return response;
        }

        Boolean exist = authClient.getUserById(request.getIdUser());

        if (exist == null) {
            return crearReservaPendiente(request, idempotencyKey);
        }

        if (!exist) {
            throw new NotFoundException("User not found");
        }

        return crearReservaCompleta(request, ReserveStatus.RESERVADO, idempotencyKey);
    }

    private ReservaResponse crearReservaPendiente(ReserveRequest request, String idempotencyKey) {

        Optional<Reserve> existing = reserveRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            Reserve reserve = existing.get();
            ReservaResponse response = new ReservaResponse();
            response.setIdReserva(reserve.getIdReserva());
            response.setReserveDate(reserve.getReserveDate());
            response.setTotalPrice(reserve.getTotalPrice());
            response.setStatus(reserve.getStatus().name());
            return response;
        }

        Reserve reserve = new Reserve();
        reserve.setIdUser(request.getIdUser());
        reserve.setReserveDate(Date.valueOf(LocalDate.now()));
        reserve.setTotalPrice(0);
        reserve.setStatus(ReserveStatus.PENDIENTE);
        reserve.setIdempotencyKey(idempotencyKey);
        reserve.setDeliveryAddress(request.getDeliveryAddress()); // ← nuevo

        Reserve savedReserve = reserveRepository.save(reserve);

        int total = 0;

        for (ProductItemRequest item : request.getProducts()) {

            Product product = productRepository.findById(item.getIdProduct())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

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

        pendienteProducer.enviarReservaPendiente(
                savedReserve.getIdReserva(),
                request.getIdUser());

        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(savedReserve.getIdReserva());
        response.setReserveDate(savedReserve.getReserveDate());
        response.setTotalPrice(savedReserve.getTotalPrice());
        response.setStatus("PENDIENTE");

        return response;
    }

    private ReservaResponse crearReservaCompleta(
            ReserveRequest request,
            ReserveStatus status,
            String idempotencyKey) {

        Optional<Reserve> existing = reserveRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            Reserve reserve = existing.get();
            ReservaResponse response = new ReservaResponse();
            response.setIdReserva(reserve.getIdReserva());
            response.setReserveDate(reserve.getReserveDate());
            response.setTotalPrice(reserve.getTotalPrice());
            response.setStatus(reserve.getStatus().name());
            return response;
        }

        Reserve reserve = new Reserve();
        reserve.setIdUser(request.getIdUser());
        reserve.setReserveDate(Date.valueOf(LocalDate.now()));
        reserve.setTotalPrice(0);
        reserve.setStatus(status);
        reserve.setIdempotencyKey(idempotencyKey);
        reserve.setDeliveryAddress(request.getDeliveryAddress()); // ← nuevo

        Reserve savedReserve = reserveRepository.save(reserve);

        int total = 0;

        for (ProductItemRequest item : request.getProducts()) {

            Product product = productRepository.findById(item.getIdProduct())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            if (product.getProductStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock available");
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

        eventPublisher.publishReservaCreated(
                savedReserve.getIdReserva(),
                savedReserve.getIdUser());

        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(savedReserve.getIdReserva());
        response.setReserveDate(savedReserve.getReserveDate());
        response.setTotalPrice(savedReserve.getTotalPrice());
        response.setStatus("RESERVADO");

        return response;
    }

    public void deleteReserve(CancelReserveRequest request) {

        Reserve reserve = reserveRepository.findById(request.getIdReserve())
                .orElseThrow(() -> new NotFoundException("Reserve not found"));

        if (!reserve.getIdUser().equals(request.getIdUser())) {
            throw new IllegalStateException("User does not own this reserve");
        }

        if (reserve.getStatus().equals(ReserveStatus.CANCELADO)) {
            throw new IllegalStateException("Reserve is already cancelled");
        }

        for (ReserveDetails detail : reserve.getReserveDetails()) {
            Product stockRecovered = detail.getProduct();
            stockRecovered.setProductStock(stockRecovered.getProductStock() + detail.getQuanty());
            productRepository.save(stockRecovered);
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
                            detail.getUnitPrice() * detail.getQuanty()))
                    .toList();

            return new ReservaDetalleResponse(
                    reserve.getIdReserva(),
                    reserve.getReserveDate(),
                    reserve.getTotalPrice(),
                    reserve.getStatus().name(),
                    reserve.getDeliveryAddress(), // ← nuevo
                    products);

        }).toList();
    }

    public void cancelReservaAdmin(Long idReserva) {

        Reserve reserve = reserveRepository.findById(idReserva)
                .orElseThrow(() -> new NotFoundException("Reserve not found"));

        if (reserve.getStatus().equals(ReserveStatus.CANCELADO)) {
            throw new IllegalStateException("Reserve is already cancelled");
        }

        for (ReserveDetails detail : reserve.getReserveDetails()) {
            Product stockRecovered = detail.getProduct();
            stockRecovered.setProductStock(stockRecovered.getProductStock() + detail.getQuanty());
            productRepository.save(stockRecovered);
        }

        reserve.setStatus(ReserveStatus.CANCELADO);
        reserveRepository.save(reserve);
        eventPublisher.publishReservaCancelled(idReserva);
    }
}