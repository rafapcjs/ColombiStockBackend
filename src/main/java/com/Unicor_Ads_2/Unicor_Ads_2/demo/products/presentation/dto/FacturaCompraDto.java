package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaCompraDto {
    private String numeroFactura;
    private String proveedorNombre;
    private LocalDate fechaCompra;
    private List<DetalleProductoDto> productos;
    private double totalFactura;  // Agregado para el total de la factura
    // Getters, setters y builder...
}
