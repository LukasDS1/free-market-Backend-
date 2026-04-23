package com.freemarket.reserva_service.service;

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
        product.setProductprice(request.getPrice());
        product.setProductStock(request.getStock());
        product.setProvider(provider);

        Product saved = productRepository.save(product);

        ProductoResponse response = new ProductoResponse();
        response.setId(saved.getIdProduct());
        response.setName(saved.getProductname());
        response.setPrice(saved.getProductprice());
        response.setStock(saved.getProductStock());
        response.setProovedorNombre(saved.getProvider().getProvidername());
        return response;

    }


    //ACTUALIZAR PRODUCTO

    public ProductoResponse updateProduct(Long id, ProductoRequest request) {
    Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException());
    
    if (request.getName() != null) {
        nameValidation(request.getName());
        product.setProductname(request.getName());
    }

    if ( request.getPrice() > 0) {
        priceValidation(request.getPrice());
        product.setProductprice(request.getPrice());
    }

    if (request.getStock() > 0) {
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
        productRepository.deleteById(id);
    }

    //validaciones

    public void nameValidation(String name){
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException();
        }

    }

    public void proovedorValidation(String name){
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException();
        }

    }

    public void priceValidation(int price){
        if(price <= 0 || price == 0){
            throw new IllegalArgumentException();
        }
    }

     public void stockValidation(int stock){
        if(stock <= 0 ){
            throw new IllegalArgumentException();
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
