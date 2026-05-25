package com.freemarket.reserva_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemarket.reserva_service.request.ProductoRequest;
import com.freemarket.reserva_service.response.ProductoResponse;
import com.freemarket.reserva_service.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api-v1/productos")
@RequiredArgsConstructor
@Tag(
    name = "Producto Controller",
    description = "Product management endpoints for the reservation system"
)
public class ProductoController {

    private final ProductService productService;

    @GetMapping("/get")
    @Operation(
        summary = "Get all products",
        description = "Returns all registered products"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Products not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<?> getProductALlProducts() {

        return ResponseEntity.ok(
            productService.findAllProducts()
        );
    }

    @PostMapping("/create")
    @Operation(
        summary = "Create product",
        description = "Creates a new product"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<ProductoResponse> createProduct(

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product data",
            required = true,
            content = @Content(
                schema = @Schema(implementation = ProductoRequest.class)
            )
        )

        @RequestBody ProductoRequest request
    ) {

        return ResponseEntity.ok(
            productService.createProduct(request)
        );
    }

    @GetMapping("/get/{id}")
    @Operation(
        summary = "Get product by id",
        description = "Returns a product using its id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<?> getProductById(

        @Parameter(
            description = "Product id",
            example = "5",
            required = true
        )

        @PathVariable Long id
    ) {

        return ResponseEntity.ok(
            productService.getProductById(id)
        );
    }

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Update product",
        description = "Partially updates a product"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<ProductoResponse> updateProduct(

        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product data to update",
            required = true,
            content = @Content(
                schema = @Schema(implementation = ProductoRequest.class)
            )
        )

        @RequestBody ProductoRequest request,

        @Parameter(
            description = "Product id",
            example = "5",
            required = true
        )

        @PathVariable Long id
    ) {

        return ResponseEntity.ok(
            productService.updateProduct(id, request)
        );
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Delete product",
        description = "Deletes a product by id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "202",
            description = "Product deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<?> deleteProduct(

        @Parameter(
            description = "Product id",
            example = "5",
            required = true
        )

        @PathVariable Long id
    ) {

        productService.deleteProductById(id);

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .build();
    }

    @PatchMapping("/activate/{id}")
@Operation(
    summary = "Activate product",
    description = "Activates a previously deactivated product"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Product activated successfully"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Product not found",
        content = @Content(mediaType = "application/json")
    )
})
public ResponseEntity<?> activateProduct(

    @Parameter(
        description = "Product id",
        example = "5",
        required = true
    )

    @PathVariable Long id
) {

    productService.activateProduct(id);

    return ResponseEntity.ok().build();
}

@GetMapping("/get/active")
public ResponseEntity<List<ProductoResponse>> getActiveProducts() {

    return ResponseEntity.ok(
        productService.findActiveProducts()
    );

}

}