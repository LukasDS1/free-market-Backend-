package com.freemarket.reserva_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.reserva_service.client.AuthClient;
import com.freemarket.reserva_service.exception.NotFoundException;
import com.freemarket.reserva_service.messaging.ReservaEventPublisher;
import com.freemarket.reserva_service.messaging.ReservaPendienteProducer;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.model.Provider;
import com.freemarket.reserva_service.model.Reserve;
import com.freemarket.reserva_service.model.ReserveDetails;
import com.freemarket.reserva_service.repository.ProductRepository;
import com.freemarket.reserva_service.repository.ReserveDetailsRepository;
import com.freemarket.reserva_service.repository.ReserveRepository;
import com.freemarket.reserva_service.request.CancelReserveRequest;
import com.freemarket.reserva_service.request.ProductItemRequest;
import com.freemarket.reserva_service.request.ReserveRequest;
import com.freemarket.reserva_service.response.ReservaResponse;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private ReserveDetailsRepository reserveDetailsRepository;

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReservaEventPublisher eventPublisher;

    @Mock
    private ReservaPendienteProducer pendienteProducer;

    @InjectMocks
    private ReservaService reservaService;

    // ── builders ──────────────────────────────────────────────────────────────

    private ReserveRequest buildReserveRequest() {
        ProductItemRequest item = new ProductItemRequest();
        item.setIdProduct(1L);
        item.setQuantity(2);
        ReserveRequest req = new ReserveRequest();
        req.setIdUser(1L);
        req.setProducts(List.of(item));
        return req;
    }

    private Product buildProduct() {
        Provider provider = new Provider();
        provider.setProvidername("TECHCORP");
        Product p = new Product();
        p.setIdProduct(1L);
        p.setProductname("Laptop");
        p.setProductprice(1000);
        p.setProductStock(10);
        p.setProvider(provider);
        return p;
    }

    private Reserve buildSavedReserve() {
        Reserve r = new Reserve();
        r.setIdReserva(1L);
        r.setIdUser(1L);
        r.setTotalPrice(0);
        return r;
    }

    private Reserve buildReserveWithDetails(Product product) {
        ReserveDetails detail = new ReserveDetails();
        detail.setQuanty(2);
        detail.setProduct(product);
        Reserve r = buildSavedReserve();
        r.setReserveDetails(List.of(detail));
        return r;
    }

    private CancelReserveRequest buildCancelRequest() {
        CancelReserveRequest req = new CancelReserveRequest();
        req.setIdReserve(1L);
        req.setIdUser(1L);
        return req;
    }

    // ── createReserva ─────────────────────────────────────────────────────────

    @Test
    void createReserva_success_returnsReservaResponse() {
        Reserve saved = buildSavedReserve();
        Product product = buildProduct();

        when(authClient.getUserById(1L)).thenReturn(true);
        when(reserveRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(reserveRepository.save(any(Reserve.class))).thenReturn(saved);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(reserveDetailsRepository.save(any(ReserveDetails.class))).thenReturn(new ReserveDetails());

        ReservaResponse response = reservaService.createReserva(buildReserveRequest(), "test-key-123");

        assertThat(response.getIdReserva()).isEqualTo(1L);
        verify(eventPublisher).publishReservaCreated(1L, 1L);
    }

    @Test
    void createReserva_userNotFound_throwsNotFoundException() {
        when(authClient.getUserById(1L)).thenReturn(false);
        when(reserveRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.createReserva(buildReserveRequest(), "test-key-123"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void createReserva_serviceUnavailable_createsPendiente() {
        Reserve saved = buildSavedReserve();
        Product product = buildProduct();

        when(authClient.getUserById(1L)).thenReturn(null);
        when(reserveRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(reserveRepository.save(any(Reserve.class))).thenReturn(saved);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reserveDetailsRepository.save(any(ReserveDetails.class))).thenReturn(new ReserveDetails());

        ReservaResponse response = reservaService.createReserva(buildReserveRequest(), "test-key-123");

        assertThat(response.getStatus()).isEqualTo("PENDIENTE");
    }

    @Test
    void createReserva_productNotFound_throwsNotFoundException() {
        when(authClient.getUserById(1L)).thenReturn(true);
        when(reserveRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(reserveRepository.save(any(Reserve.class))).thenReturn(buildSavedReserve());
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.createReserva(buildReserveRequest(), "test-key-123"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void createReserva_insufficientStock_throwsIllegalArgument() {
        Product product = buildProduct();
        product.setProductStock(1);

        when(authClient.getUserById(1L)).thenReturn(true);
        when(reserveRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        when(reserveRepository.save(any(Reserve.class))).thenReturn(buildSavedReserve());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> reservaService.createReserva(buildReserveRequest(), "test-key-123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock available");
    }

    @Test
    void createReserva_duplicateKey_returnsSameReserva() {
        Reserve existing = buildSavedReserve();

        when(reserveRepository.findByIdempotencyKey("test-key-123")).thenReturn(Optional.of(existing));

        ReservaResponse response = reservaService.createReserva(buildReserveRequest(), "test-key-123");

        assertThat(response.getIdReserva()).isEqualTo(1L);
        verify(reserveRepository, never()).save(any());
    }
}