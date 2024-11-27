package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductItemDto {
    private String codeProduct; // ID del producto
    private int quantity;   // Cantidad deseada del producto
}