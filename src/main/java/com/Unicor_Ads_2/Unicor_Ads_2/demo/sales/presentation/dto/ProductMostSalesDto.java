package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductMostSalesDto {


    private Long productId;
    private String productName;
    private String category;
    private String provider;
    private Double price;
    private Long totalQuantity;

}
