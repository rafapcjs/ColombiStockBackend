package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.factory;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory {

public  ProductDTO createProductDTO(Products products){

    return ProductDTO.builder()
            .code(products.getCode())
            .price(products.getPrice())
            .description(products.getDescription())
            .name(products.getName())
            .stock(products.getStock())
            .stockMin(products.getStockMin())
            .unit(products.getUnit())
            .codigoCategoria(products.getCategory() != null ? products.getCategory().getCode() : null)

            .build();
    }

}
