package com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.configurations;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración general de Swagger/OpenAPI para documentar los endpoints de la API REST de ColombiStock.
 *
 * Proyecto desarrollado para las materias:
 * - Base de Datos II
 * - Emprendimiento e Innovación Tecnológica
 *
 * Universidad de Córdoba - Ingeniería de Sistemas
 */
@OpenAPIDefinition(
        info = @Info(
                title = "ColombiStock API",
                description = "API REST Backend para la gestión de inventarios de tiendas.\n\n" +
                        "Proyecto académico desarrollado para las materias:\n" +
                        "- Base de Datos II\n" +
                        "- Emprendimiento e Innovación Tecnológica",
                termsOfService = "mailto:rafaelcorredorgambin1@gmail.com",
                version = "1.0.0",
                contact = @Contact(
                        name = "Grupo universitario - Universidad de Córdoba - Ingeniería de Sistemas",
                        url = "https://www.linkedin.com/in/rafael-alfonso-corredor-gamb%C3%ADn-67a329274/",
                        email = "rafaelcorredorgambin1@gmail.com"
                ),
                license = @License(
                        name = "Proyecto académico: Base de Datos II y Emprendimiento e Innovación Tecnológica",
                        url = "https://www.linkedin.com/in/rafael-alfonso-corredor-gamb%C3%ADn-67a329274/"
                )
        ),
        servers = {
                @Server(
                        description = "Servidor de Desarrollo",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Servidor de Producción",
                        url = "http://colombistock.com"
                )
        }
)
@Configuration
public class SwaggerConfig {

    /**
     * Configuración de seguridad para habilitar el uso de JWT (Bearer Token) en la documentación Swagger.
     * Permite enviar tokens para proteger endpoints.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
