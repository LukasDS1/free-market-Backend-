package com.freemarket.locations_service.config;

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
    public OpenAPI locationsServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Locations Service")
                .version("1.0")
                .description("Documentación de API REST para gestión de ubicaciones de usuarios con geocodificación")
                .contact(new Contact().name("Contact ADMIN")))
            .components(new Components()
                .addSchemas("MapsDTO", new Schema<>()
                    .name("MapsDTO")
                    .description("DTO con datos de geolocalización retornados por el servicio de mapas")
                    .addProperty("latitude", new Schema<>().type("number").format("double").description("Latitud").example(-33.4569))
                    .addProperty("longitude", new Schema<>().type("number").format("double").description("Longitud").example(-70.6483))
                    .addProperty("formattedAddress", new Schema<>().type("string").description("Dirección formateada").example("Av. Libertador Bernardo O'Higgins 1234, Santiago"))
                    .addProperty("comunaNombre", new Schema<>().type("string").description("Nombre de la comuna").example("Santiago"))
                    .addProperty("regionNombre", new Schema<>().type("string").description("Nombre de la región").example("Región Metropolitana"))));
    }
}