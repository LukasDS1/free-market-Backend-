package com.freemarket.reserva_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;

@Configuration
public class SwaggerConfig {

    @Bean 
    public OpenAPI reservaServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Reserva Service")
                .version("1.0")
                .description("Documentación de API REST para gestión de productos y reservas")
                .contact(new Contact().name("Contact ADMIN")))
            .components(new Components()
                .addSchemas("ProductItemRequest", new Schema<>()
                    .name("ProductItemRequest")
                    .description("Item de producto dentro de una reserva")
                    .addProperty("idProduct", new Schema<>().type("integer").format("int64").example(5))
                    .addProperty("quantity", new Schema<>().type("integer").example(3))));
    }
}