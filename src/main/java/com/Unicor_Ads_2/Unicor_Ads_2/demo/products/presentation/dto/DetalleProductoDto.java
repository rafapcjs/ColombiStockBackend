package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleProductoDto {
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double totalPorProducto;  // Agregado para el total por producto
    // Getters, setters y builder...
}
