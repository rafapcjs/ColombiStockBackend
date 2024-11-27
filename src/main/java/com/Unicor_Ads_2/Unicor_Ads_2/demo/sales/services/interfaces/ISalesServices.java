package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.interfaces;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.InvoiceDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.ProductItemDto;

import java.util.List;
import java.util.UUID;

public interface ISalesServices {

      InvoiceDTO createSale(List<ProductItemDto> productItems);
      void cancelSale (UUID saleId);

}
