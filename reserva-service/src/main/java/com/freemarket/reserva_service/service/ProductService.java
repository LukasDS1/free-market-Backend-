package com.freemarket.reserva_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.freemarket.reserva_service.exception.NotFoundException;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.model.Provider;
import com.freemarket.reserva_service.repository.ProductRepository;
import com.freemarket.reserva_service.repository.ProviderRepository;
import com.freemarket.reserva_service.request.ProductoRequest;
import com.freemarket.reserva_service.response.ProductoResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProviderRepository providerRepository;

    public List<ProductoResponse> findAllProducts() {

        List<Product> products = productRepository.findAll();
        List<ProductoResponse> responses = new ArrayList<>();

        for (Product product : products) {

            ProductoResponse response = new ProductoResponse();

            response.setId(product.getIdProduct());
            response.setName(product.getProductname());
            response.setPrice(product.getProductprice());
            response.setProovedorNombre(product.getProvider().getProvidername());
            response.setStock(product.getProductStock());
            response.setUrl(product.getUrl());

            responses.add(response);
        }

        return responses;
    }

    public ProductoResponse createProduct(ProductoRequest request) {

        String providerName = request.getProovedorNombre().trim().toUpperCase();

        providerValidation(providerName);
        nameValidation(request.getName());
        priceValidation(request.getPrice());
        stockValidation(request.getStock());

        Provider provider = findOrCreateProvider(providerName);

        Product product = new Product();

        product.setProductname(request.getName());
        product.setUrl(request.getUrl());
        product.setProductprice(request.getPrice());
        product.setProductStock(request.getStock());
        product.setProvider(provider);

        Product saved = productRepository.save(product);

        ProductoResponse response = new ProductoResponse();

        response.setId(saved.getIdProduct());
        response.setName(saved.getProductname());
        response.setPrice(saved.getProductprice());
        response.setStock(saved.getProductStock());
        response.setUrl(saved.getUrl());
        response.setProovedorNombre(saved.getProvider().getProvidername());

        return response;
    }

    public ProductoResponse updateProduct(Long id, ProductoRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (request.getName() != null) {
            nameValidation(request.getName());
            product.setProductname(request.getName());
        }

        if (request.getUrl() != null) {
            product.setUrl(request.getUrl());
        }

        if (request.getPrice() != null) {
            priceValidation(request.getPrice());
            product.setProductprice(request.getPrice());
        }

        if (request.getStock() != null) {
            stockValidation(request.getStock());
            product.setProductStock(request.getStock());
        }

        if (request.getProovedorNombre() != null) {

            String providerName = request.getProovedorNombre().trim().toUpperCase();

            providerValidation(providerName);

            Provider provider = findOrCreateProvider(providerName);

            product.setProvider(provider);
        }

        Product saved = productRepository.save(product);

        ProductoResponse response = new ProductoResponse();

        response.setId(saved.getIdProduct());
        response.setName(saved.getProductname());
        response.setPrice(saved.getProductprice());
        response.setStock(saved.getProductStock());
        response.setUrl(saved.getUrl());
        response.setProovedorNombre(saved.getProvider().getProvidername());

        return response;
    }

    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public void deleteProductById(Long id) {

        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found");
        }

        productRepository.deleteById(id);
    }

    public void nameValidation(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
    }

    public void providerValidation(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Provider name cannot be empty");
        }
    }

    public void priceValidation(int price) {

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
    }

    public void stockValidation(int stock) {

        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }

    private Provider findOrCreateProvider(String name) {

        return providerRepository.findByProvidername(name)
                .orElseGet(() -> {

                    Provider newProvider = new Provider();

                    newProvider.setProvidername(name);

                    return providerRepository.save(newProvider);
                });
    }
}