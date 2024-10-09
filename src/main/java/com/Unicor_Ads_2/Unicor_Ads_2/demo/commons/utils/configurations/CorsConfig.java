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
                registry.addMapping("*")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .exposedHeaders("*");

                registry.addMapping("*")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .exposedHeaders("*");

                registry.addMapping("*")
                        .allowedOrigins("*")
                        .allowedMethods("*");
            }
        };
    }
}