package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.interfaces;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.FacturaCompraDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload.FacturaCompraPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

// IFacturaCompraService.java
public interface IFacturaCompraService {
    void registrarFactura(FacturaCompraPayload payload);
    Page<FacturaCompraDto> listarFacturas(Pageable pageable);
    void editarFactura(String numeroFactura, FacturaCompraPayload payload);


}
