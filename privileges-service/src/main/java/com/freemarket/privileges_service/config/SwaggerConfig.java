package com.freemarket.privileges_service.config;


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
    public OpenAPI privilegesServiceOpenAPI() {

        Schema<?> moduloSchema = new Schema<>()
            .name("Modulo")
            .description("Entidad que representa un módulo del sistema")
            .addProperty("moduloId", new Schema<>().type("integer").format("int64").description("ID del módulo").example(1))
            .addProperty("moduloname", new Schema<>().type("string").description("Nombre del módulo").example("Inventario"));

        Schema<?> privilegesSchema = new Schema<>()
            .name("Privileges")
            .description("Entidad que representa un privilegio asociado a un módulo")
            .addProperty("privilegesId", new Schema<>().type("integer").format("int64").description("ID del privilegio").example(1))
            .addProperty("privilegeName", new Schema<>().type("string").description("Nombre del privilegio").example("CREAR_PRODUCTO"))
            .addProperty("modulo", new Schema<>().$ref("#/components/schemas/Modulo"));

        Schema<?> rolPrivilegesSchema = new Schema<>()
            .name("RolPrivileges")
            .description("Relación entre un rol y sus privilegios asignados")
            .addProperty("id", new Schema<>().type("integer").format("int64").description("ID del registro").example(1))
            .addProperty("roleId", new Schema<>().type("integer").format("int64").description("ID del rol").example(3))
            .addProperty("privilege", new Schema<>().$ref("#/components/schemas/Privileges"));

        return new OpenAPI()
            .info(new Info()
                .title("API Privileges Service")
                .version("1.0")
                .description("Documentación de API REST para gestión de módulos y privilegios del sistema")
                .contact(new Contact().name("Contact ADMIN")))
            .components(new Components()
                .addSchemas("Modulo", moduloSchema)
                .addSchemas("Privileges", privilegesSchema)
                .addSchemas("RolPrivileges", rolPrivilegesSchema));
    }
}