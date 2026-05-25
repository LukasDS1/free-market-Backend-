package com.freemarket.reserva_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.reserva_service.exception.NotFoundException;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.model.Provider;
import com.freemarket.reserva_service.repository.ProductRepository;
import com.freemarket.reserva_service.repository.ProviderRepository;
import com.freemarket.reserva_service.request.ProductoRequest;
import com.freemarket.reserva_service.response.ProductoResponse;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProductService productService;

    private ProductoRequest buildRequest() {
        ProductoRequest req = new ProductoRequest();
        req.setName("Laptop");
        req.setUrl("http://img.com/laptop.jpg");
        req.setPrice(1000);
        req.setStock(10);
        req.setProovedorNombre("techcorp");
        return req;
    }

    private Provider buildProvider() {
        Provider p = new Provider();
        p.setProvidername("TECHCORP");
        return p;
    }

    private Product buildSavedProduct(Provider provider) {
        Product p = new Product();
        p.setIdProduct(1L);
        p.setProductname("Laptop");
        p.setProductprice(1000);
        p.setProductStock(10);
        p.setProvider(provider);
        return p;
    }

    // ── createProduct ─────────────────────────────────────────────────────────

    @Test
    void createProduct_success_returnsProductoResponse() {
        Provider provider = buildProvider();
        Product saved = buildSavedProduct(provider);

        when(providerRepository.findByProvidername("TECHCORP")).thenReturn(Optional.of(provider));
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductoResponse response = productService.createProduct(buildRequest());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Laptop");
        assertThat(response.getPrice()).isEqualTo(1000);
        assertThat(response.getProovedorNombre()).isEqualTo("TECHCORP");
    }

    @Test
    void createProduct_providerNotFound_createsNewProvider() {
        Provider newProvider = buildProvider();
        Product saved = buildSavedProduct(newProvider);

        when(providerRepository.findByProvidername("TECHCORP")).thenReturn(Optional.empty());
        when(providerRepository.save(any(Provider.class))).thenReturn(newProvider);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductoResponse response = productService.createProduct(buildRequest());

        assertThat(response.getProovedorNombre()).isEqualTo("TECHCORP");
        verify(providerRepository).save(any(Provider.class));
    }

    @Test
    void createProduct_emptyName_throwsIllegalArgument() {
        ProductoRequest req = buildRequest();
        req.setName("");

        assertThatThrownBy(() -> productService.createProduct(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product name cannot be empty");
    }

    @Test
    void createProduct_emptyProvider_throwsIllegalArgument() {
        ProductoRequest req = buildRequest();
        req.setProovedorNombre("");

        assertThatThrownBy(() -> productService.createProduct(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provider name cannot be empty");
    }

    @Test
    void createProduct_zeroPrice_throwsIllegalArgument() {
        ProductoRequest req = buildRequest();
        req.setPrice(0);

        assertThatThrownBy(() -> productService.createProduct(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be greater than zero");
    }

    @Test
    void createProduct_negativeStock_throwsIllegalArgument() {
        ProductoRequest req = buildRequest();
        req.setStock(-1);

        assertThatThrownBy(() -> productService.createProduct(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock cannot be negative");
    }

    // ── updateProduct ─────────────────────────────────────────────────────────

    @Test
    void updateProduct_success_returnsUpdatedResponse() {
        Provider provider = buildProvider();
        Product existing = buildSavedProduct(provider);
        Product saved = buildSavedProduct(provider);
        saved.setProductname("Laptop Pro");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductoRequest req = new ProductoRequest();
        req.setName("Laptop Pro");

        ProductoResponse response = productService.updateProduct(1L, req);

        assertThat(response.getName()).isEqualTo("Laptop Pro");
    }

    @Test
    void updateProduct_productNotFound_throwsNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(99L, buildRequest()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    // ── deleteProductById ─────────────────────────────────────────────────────

    @Test
    void deleteProductById_success_deactivatesProduct() {
        Provider provider = buildProvider();
        Product existing = buildSavedProduct(provider);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        productService.deleteProductById(1L);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProductById_productNotFound_throwsNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProductById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found");
    }
}