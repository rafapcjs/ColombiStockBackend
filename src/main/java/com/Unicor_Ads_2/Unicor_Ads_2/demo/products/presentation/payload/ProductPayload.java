package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter

public class ProductPayload {

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String name;

    @NotNull(message = "La fecha de fabricación no puede ser nula")
    private LocalDate fechaFabricacion;  // Fecha de fabricación del producto

    private LocalDate vencimiento;       // Fecha de vencimiento proporcionada por el usuario

    private String description;

    @NotNull(message = "El precio de venta no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor que cero")
    private Double price;  // Este es el precio de venta, lo ingresa el usuario

    @NotNull(message = "El stock no puede ser nulo")
    @DecimalMin(value = "0", message = "La cantidad en stock debe ser mayor o igual que cero")
    private int stockMin;  // Este valor lo ingresa el usuario como cantidad mínima

    private String unit;
    private String code; // Código único del producto

    @NotNull(message = "El ID de categoría no puede ser nulo")
    private String codigoCategoria;
    @NotNull(message = "El dni del proveedor no puede ser nulo")
    private String dni_provedor;
}
