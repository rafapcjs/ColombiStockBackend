package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.dto;

import lombok.Builder;

@Builder
public record SupplierDto(
        String name,
        String lastName,
        String dni,
        String email,
        String phone

) {
}
