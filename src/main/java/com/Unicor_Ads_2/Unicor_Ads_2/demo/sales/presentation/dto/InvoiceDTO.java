package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceDTO {
    private UUID saleId;
    private Date saleDate;
    private double totalAmount;
    private List<InvoiceDetailDTO> details;
}