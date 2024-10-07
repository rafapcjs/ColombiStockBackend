package com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
@OpenAPIDefinition(
        info = @Info(
                title = "Colombi Stock",
                description = "API REST Backend for the management of the stock of a store",
                termsOfService = "rafaelcorredorgambin1@gmail.com",
                version = "1.0.0",
                contact = @Contact(
                        name = "Grupo universitario Universidad de Córdoba Ing. de Sistemas",
                        url = "https://www.linkedin.com/in/rafael-alfonso-corredor-gamb%C3%ADn-67a329274/",
                        email = "rafaelcorredorgambin1@gmail.com"
                ),
                license = @License(
                        name = "Unicordoba Análisis y Diseño de Sistemas Informáticos",
                        url = "https://www.linkedin.com/in/rafael-alfonso-corredor-gamb%C3%ADn-67a329274/"
                )
        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "PROD SERVER",
                        url = "http://colombistock.com"
                )
        }
)

public class SwaggerConfig {
}
