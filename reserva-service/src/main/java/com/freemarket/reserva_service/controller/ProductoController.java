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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api-v1/productos")
@RequiredArgsConstructor
@Tag(name = "Producto Controller", description = "Endpoints para gestión de productos del sistema de reservas")
public class ProductoController {

    private final ProductService productService;

    @PostMapping("/create")
    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ProductoResponse> createProduct(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del producto a crear", required = true,
            content = @Content(schema = @Schema(implementation = ProductoRequest.class)))
        @RequestBody ProductoRequest request) {
        return ResponseEntity.ok().body(productService.createProduct(request));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Retorna los datos de un producto según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> getProductById(
        @Parameter(description = "ID del producto a consultar", example = "5", required = true)
        @PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getProductById(id));
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza parcialmente los datos de un producto por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ProductoResponse> updateProduct(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos a actualizar del producto", required = true,
            content = @Content(schema = @Schema(implementation = ProductoRequest.class)))
        @RequestBody ProductoRequest request,
        @Parameter(description = "ID del producto a actualizar", example = "5", required = true)
        @PathVariable Long id) {
        return ResponseEntity.ok().body(productService.updateProduct(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> deleteProduct(
        @Parameter(description = "ID del producto a eliminar", example = "5", required = true)
        @PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}