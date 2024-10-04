package com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig  {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
