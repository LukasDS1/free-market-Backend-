package com.freemarket.delivery_service.config;

import java.util.List;

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
    public OpenAPI deliveryServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Delivery Service")
                .version("1.0")
                .description("Documentación de API REST para gestión y seguimiento de deliveries")
                .contact(new Contact().name("Contact ADMIN")))
            .components(new Components()
                .addSchemas("Delivery", new Schema<>()
                    .name("Delivery")
                    .description("Entidad que representa un delivery")
                    .addProperty("idDelivery", new Schema<>().type("integer").format("int64").description("ID del delivery").example(1))
                    .addProperty("status", new Schema<>().type("string").description("Estado del delivery")
                        ._enum(List.of("PENDIENTE", "EN_CAMINO", "ENTREGADO", "CANCELADO")).example("EN_CAMINO")))
                .addSchemas("DeliveryDetails", new Schema<>()
                    .name("DeliveryDetails")
                    .description("Detalles del delivery")
                    .addProperty("idDeliveryDetails", new Schema<>().type("integer").format("int64").description("ID del detalle").example(1))
                    .addProperty("deliveryBeginDate", new Schema<>().type("string").format("date").description("Fecha de inicio").example("2026-05-07"))
                    .addProperty("deliveryEndDate", new Schema<>().type("string").format("date").description("Fecha estimada de entrega").example("2026-05-10"))
                    .addProperty("idReserva", new Schema<>().type("integer").format("int64").description("ID de la reserva asociada").example(10))));
    }
}
