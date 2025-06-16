package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class SaleDTO {
    private UUID uuid;
    private Date saleDate;
    private double totalAmount;
    private List<ProductDTO> products;

    // Getters y Setters
@Getter
@Setter
    public static class ProductDTO {
        private UUID uuid;
        private String name;
        private double price;
        private int stock;

        // Getters y Setters
    }
}
