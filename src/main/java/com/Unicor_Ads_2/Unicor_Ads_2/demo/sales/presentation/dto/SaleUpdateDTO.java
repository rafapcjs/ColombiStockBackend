package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SaleUpdateDTO {
    private Date saleDate;
    private double totalAmount;
    private List<ProductItemDTO> products;

  @Getter
  @Setter// Getters y setters

    public static class ProductItemDTO {
        private UUID productId;
        private int quantity;

        // Getters y setters
    }
}
