package com.freemarket.reserva_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.reserva_service.request.ProductoRequest;
import com.freemarket.reserva_service.response.ProductoResponse;
import com.freemarket.reserva_service.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("api-v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductoResponse> createProduct(@RequestBody ProductoRequest request) {

        return ResponseEntity.ok().body(productService.createProduct(request));
       
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getProductById(id));
    
    
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<ProductoResponse> updateProduct(@RequestBody ProductoRequest request, @PathVariable Long id) {
        return ResponseEntity.ok().body(productService.updateProduct(id, request));
}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
   
             productService.deleteProductById(id);
             return ResponseEntity.status(HttpStatus.ACCEPTED).build();

    }







}