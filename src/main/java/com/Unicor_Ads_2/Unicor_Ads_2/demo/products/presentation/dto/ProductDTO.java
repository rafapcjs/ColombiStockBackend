package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProductDTO(
        UUID id,
        String name,
        String description,
        Double price,
        Double purchasePrice,
        int stock,
        int stockMin,
        String unit,
        String code,
        String nameCategory,
        String nameSuppliers
) {}

