package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.factory;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.dto.SupplierDto;
import org.springframework.stereotype.Component;

@Component
public class SuppliersFactory {

    public SupplierDto createSupplierDTO(Suppliers suppliers) {

        return SupplierDto.builder()
                .name(suppliers.getName())
                .lastName(suppliers.getLastName())
                .phone(suppliers.getPhone())
                .email(suppliers.getEmail())
                .dni(suppliers.getDni())
                .build();
    }
}
