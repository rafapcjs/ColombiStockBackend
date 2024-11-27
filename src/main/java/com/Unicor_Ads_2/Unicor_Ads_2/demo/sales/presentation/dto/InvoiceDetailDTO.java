package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceDetailDTO {
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
}