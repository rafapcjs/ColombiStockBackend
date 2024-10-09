package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter

public class ProductPayload {


    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String name;

    private String description;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
    private Double price;

    @NotNull(message = "El precio de compra no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de compra debe ser mayor que cero")
    private Double purchasePrice;

    @NotNull(message = "El stock no puede ser nulo")
    @DecimalMin(value = "0", message = "La cantidad en stock debe ser mayor o igual que cero")
    private int stock;

    @NotNull(message = "La cantidad mínima de stock no puede ser nula")
    @DecimalMin(value = "0", message = "La cantidad mínima de stock debe ser mayor o igual que cero")
    private int stockMin;

    private String unit;
     private String code; // Código único del producto



    @NotNull(message = "El ID de categoría no puede ser nulo")
    private String codigoCategoria;
}
