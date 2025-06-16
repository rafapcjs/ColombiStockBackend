package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleProfitDetailDTO {
    private UUID saleId;
    private Date saleDate;
    private List<SaleProductLineDetailDTO> lines;
    private double totalRevenue;
    private double totalCost;
    private double totalProfit;
}