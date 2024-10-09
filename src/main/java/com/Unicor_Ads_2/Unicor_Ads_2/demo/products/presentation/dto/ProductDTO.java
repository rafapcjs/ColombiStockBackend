package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto;

import lombok.Builder;

@Builder
public record ProductDTO(
        Long id,
        String name,
        String description,
        Double price,
        Double purchasePrice,
        int stock,
        int stockMin,
        String unit,
        String code,
        String codigoCategoria
) {}

