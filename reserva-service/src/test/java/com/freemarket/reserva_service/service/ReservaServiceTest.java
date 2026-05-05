package com.freemarket.reserva_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import com.freemarket.reserva_service.exception.ServiceUnavailableException;
import com.freemarket.reserva_service.messaging.ReservaEventPublisher;
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

    @InjectMocks
    private ReservaService reservaService;



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

    @Test
    void createReserva_success_returnsReservaResponse() {
        Reserve saved = buildSavedReserve();
        Product product = buildProduct();

        when(authClient.getUserById(1L)).thenReturn(true);
        when(reserveRepository.save(any(Reserve.class))).thenReturn(saved);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(reserveDetailsRepository.save(any(ReserveDetails.class))).thenReturn(new ReserveDetails());

        ReservaResponse response = reservaService.createReserva(buildReserveRequest());

        assertThat(response.getIdReserva()).isEqualTo(1L);
        verify(eventPublisher).publishReservaCreated(1L);
    }

    @Test
    void createReserva_userNotFound_throwsIllegalArgument() {
        when(authClient.getUserById(1L)).thenReturn(false);

        assertThatThrownBy(() -> reservaService.createReserva(buildReserveRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not Found");
    }

    @Test
    void createReserva_serviceUnavailable_throwsServiceUnavailable() {
        when(authClient.getUserById(1L)).thenReturn(null);

        assertThatThrownBy(() -> reservaService.createReserva(buildReserveRequest()))
                .isInstanceOf(ServiceUnavailableException.class);
    }

    @Test
    void createReserva_productNotFound_throwsIllegalArgument() {
        when(authClient.getUserById(1L)).thenReturn(true);
        when(reserveRepository.save(any(Reserve.class))).thenReturn(buildSavedReserve());
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.createReserva(buildReserveRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Producto no encontrado");
    }

    @Test
    void createReserva_insufficientStock_throwsIllegalArgument() {
        Product product = buildProduct();
        product.setProductStock(1); // request pide 2

        when(authClient.getUserById(1L)).thenReturn(true);
        when(reserveRepository.save(any(Reserve.class))).thenReturn(buildSavedReserve());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> reservaService.createReserva(buildReserveRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("suficientes");
    }



    @Test
    void deleteReserve_success_deletesAndPublishesEvent() {
        Product product = buildProduct();
        Reserve reserve = buildReserveWithDetails(product);

        when(reserveRepository.findById(1L)).thenReturn(Optional.of(reserve));

        reservaService.deleteReserve(buildCancelRequest());

        verify(reserveRepository).delete(reserve);
        verify(eventPublisher).publishReservaCancelled(1L);
    }

    @Test
    void deleteReserve_userMismatch_throwsIllegalArgument() {
        Reserve reserve = buildSavedReserve();
        reserve.setIdUser(99L); // usuario distinto

        when(reserveRepository.findById(1L)).thenReturn(Optional.of(reserve));

        assertThatThrownBy(() -> reservaService.deleteReserve(buildCancelRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no coincide");
    }

}
