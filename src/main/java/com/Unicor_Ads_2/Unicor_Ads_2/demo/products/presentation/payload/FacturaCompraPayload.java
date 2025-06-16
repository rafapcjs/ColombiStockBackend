package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

// FacturaCompraPayload.java
@Builder
public record FacturaCompraPayload(
        String numeroFactura,
        LocalDate fechaCompra,
        String proveedorDni,
        List<DetalleProductoPayload> productos
) {}

