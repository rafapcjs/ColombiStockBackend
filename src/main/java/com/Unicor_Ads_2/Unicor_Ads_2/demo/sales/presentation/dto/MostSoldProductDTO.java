package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MostSoldProductDTO {
    private String productName;
    private String productCode;
    private Long totalQuantity;

}