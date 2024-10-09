package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.service.interfaces;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.SupplierPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.dto.SupplierDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ISuppliersServices {

    void saveSupplier(SupplierPayload supplierPayload);

    void updateSupplier(SupplierPayload supplierPayload, String dni);

    Optional<SupplierDto> findSuppliersByEmail(String email);

    Optional<SupplierDto> findSuppliersByPhone(String phone);

    Page<SupplierDto> findAllSuppliers(Pageable pageable);

    String delete(String dni);


}
