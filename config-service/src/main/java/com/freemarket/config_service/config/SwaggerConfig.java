package com.freemarket.config_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;

@Configuration
public class SwaggerConfig {

   @Bean
public OpenAPI configServiceOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("API Config Service")
            .version("1.0")
            .description("Documentación de API REST para gestión de configuración visual de comercios")
            .contact(new Contact().name("Contact ADMIN")))
        .components(new Components()
            .addSchemas("Configuration", new ModelConverters().resolveAsResolvedSchema(
                new AnnotatedType(Configuration.class)).schema));
}

}
