package com.freemarket.reserva_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freemarket.reserva_service.exception.GlobalExceptionHandler;
import com.freemarket.reserva_service.model.Product;
import com.freemarket.reserva_service.request.ProductoRequest;
import com.freemarket.reserva_service.response.ProductoResponse;
import com.freemarket.reserva_service.service.ProductService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ProductoController.class)
@Import(GlobalExceptionHandler.class)
public class ProductoControllerTest {

     @MockitoBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private ProductoRequest buildRequest() {
        ProductoRequest req = new ProductoRequest();
        req.setName("Laptop");
        req.setUrl("http://img.com/laptop.jpg");
        req.setPrice(1000);
        req.setStock(10);
        req.setProovedorNombre("TECHCORP");
        return req;
    }

    private ProductoResponse buildResponse() {
        ProductoResponse res = new ProductoResponse();
        res.setId(1L);
        res.setName("Laptop");
        res.setPrice(1000);
        res.setStock(10);
        res.setProovedorNombre("TECHCORP");
        return res;
    }

       @Test
    void createProduct_success_returnsOk() throws Exception {
        when(productService.createProduct(any(ProductoRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api-v1/productos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void createProduct_invalidData_returns400() throws Exception {
        when(productService.createProduct(any(ProductoRequest.class)))
                .thenThrow(new IllegalArgumentException("El nombre no puede estar vacio"));

        mockMvc.perform(post("/api-v1/productos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest());
    }



    @Test
    void getProductById_success_returnsOk() throws Exception {
        when(productService.getProductById(1L)).thenReturn(new Product());

        mockMvc.perform(get("/api-v1/productos/get/1"))
                .andExpect(status().isOk());
    }



    @Test
    void updateProduct_success_returnsOk() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductoRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(patch("/api-v1/productos/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void updateProduct_productNotFound_returns400() throws Exception {
        when(productService.updateProduct(eq(99L), any(ProductoRequest.class)))
                .thenThrow(new IllegalArgumentException("Producto no encontrado"));

        mockMvc.perform(patch("/api-v1/productos/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest());
    }



    @Test
    void deleteProduct_success_returnsAccepted() throws Exception {
        doNothing().when(productService).deleteProductById(1L);

        mockMvc.perform(delete("/api-v1/productos/delete/1"))
                .andExpect(status().isAccepted());
    }

    @Test
    void deleteProduct_productNotFound_returns400() throws Exception {
        doThrow(new IllegalArgumentException("Producto no encontrado"))
                .when(productService).deleteProductById(99L);

        mockMvc.perform(delete("/api-v1/productos/delete/99"))
                .andExpect(status().isBadRequest());
    }




}
