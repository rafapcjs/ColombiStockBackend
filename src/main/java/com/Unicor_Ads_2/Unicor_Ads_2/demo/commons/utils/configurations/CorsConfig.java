package com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permitir todas las rutas
                        .allowedOrigins("http://localhost:3000") // Permitir solicitudes solo desde localhost:3000
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                        .allowedHeaders("*") // Permitir todos los encabezados
                        .exposedHeaders("Authorization") // Exponer encabezados específicos si es necesario
                        .allowCredentials(true); // Permitir cookies y credenciales (opcional)
            }
        };
    }
}
