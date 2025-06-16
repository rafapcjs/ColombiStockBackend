package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitReportDTO {
    private String periodLabel;
    private List<SaleProfitDetailDTO> details;
    private double totalRevenue;
    private double totalCost;
    private double totalProfit;
}