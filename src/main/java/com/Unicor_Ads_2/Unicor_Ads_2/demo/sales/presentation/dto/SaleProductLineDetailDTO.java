package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleProductLineDetailDTO {
    private String productCode;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double unitCost;
    private double revenueLine;
    private double costLine;
    private double profitLine;
}