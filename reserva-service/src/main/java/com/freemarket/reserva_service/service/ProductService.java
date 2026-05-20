package com.freemarket.reserva_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
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


    public List<ProductoResponse> findAllProducts(){
        
        List<Product> productos = productRepository.findAll();
        List<ProductoResponse> productoResponses = new ArrayList<>();

        for (Product p : productos){
        ProductoResponse response = new ProductoResponse();
        response.setId(p.getIdProduct());
        response.setName(p.getProductname());
        response.setPrice(p.getProductprice());
        response.setProovedorNombre(p.getProvider().getProvidername());
        response.setStock(p.getProductStock());
        response.setUrl(p.getUrl());
        productoResponses.add(response);
        }
        
        return productoResponses ;
    }

    //CREAR PRODUCTO
    public ProductoResponse createProduct(ProductoRequest request){
        
        String providerNombre = request.getProovedorNombre().trim().toUpperCase();

        proovedorValidation(providerNombre);
        nameValidation(request.getName());
        priceValidation(request.getPrice());
        stockValidation(request.getStock());
        
        Product product = new Product();

        Provider provider = findOrCreateProvider(providerNombre);
        
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


    //ACTUALIZAR PRODUCTO

    public ProductoResponse updateProduct(Long id, ProductoRequest request) {
    Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    
    if (request.getName() != null) {
        nameValidation(request.getName());
        product.setProductname(request.getName());
    }

    if (request.getUrl() != null) {
        product.setUrl(request.getUrl());
    }


    if ( request.getPrice() != null) {
        priceValidation(request.getPrice());
        product.setProductprice(request.getPrice());
    }

    if (request.getStock() != null) {
        stockValidation(request.getStock());
        product.setProductStock(request.getStock());
    }

    if (request.getProovedorNombre() != null) {
        String providerNombre = request.getProovedorNombre().trim().toUpperCase();
        proovedorValidation(providerNombre);
        Provider provider = findOrCreateProvider(providerNombre);
        product.setProvider(provider);
    }

    Product saved = productRepository.save(product);

    ProductoResponse response = new ProductoResponse();
    response.setId(saved.getIdProduct());
    response.setName(saved.getProductname());
    response.setPrice(saved.getProductprice());
    response.setStock(saved.getProductStock());
    response.setProovedorNombre(saved.getProvider().getProvidername());
    return response;
}


    //Buscar producto por ID
    public Product getProductById(Long id){
        return productRepository.findById(id).orElseThrow();
    }

    //Eliminar Producto por id
    public void deleteProductById(Long id){
         if (!productRepository.existsById(id)) {
        throw new IllegalArgumentException("Producto no encontrado");
    }
    productRepository.deleteById(id);
    }

    //validaciones

    public void nameValidation(String name){
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }

    }

    public void proovedorValidation(String name){
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("El nombre del prooveedor no puede estar vacio");
        }

    }

    public void priceValidation(int price){
        if(price <= 0 || price == 0){
            throw new IllegalArgumentException("El precio no pede ser menor a 0 y/o 0");
        }
    }

     public void stockValidation(int stock){
        if(stock < 0 ){
            throw new IllegalArgumentException("El stock no puede ser menor a 0");
        }
    }

    private Provider findOrCreateProvider(String nombre) {
    return providerRepository.findByProvidername(nombre)
        .orElseGet(() -> { Provider nuevo = new Provider();
            nuevo.setProvidername(nombre);
            return providerRepository.save(nuevo);
        });
}

}
