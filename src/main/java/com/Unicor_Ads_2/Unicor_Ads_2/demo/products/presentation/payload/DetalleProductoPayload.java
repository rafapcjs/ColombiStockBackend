package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload;

import lombok.Builder;

import java.util.Date;

@Builder
public record DetalleProductoPayload(
        String productCode,
        int cantidad,
       Double precioUnitario
) {}
