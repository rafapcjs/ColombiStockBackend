package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.factory;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.InvoiceDetailDTO;
import org.springframework.stereotype.Component;

@Component
public class SalesFactory {


        public InvoiceDetailDTO createInvoiceDetailDTO(Products products, double totalPrice, int quantity) {

            return InvoiceDetailDTO.builder()
                    .productName(products.getName())
                    .quantity(quantity) // Usar el parámetro quantity en lugar de item.getQuantity()
                    .unitPrice(products.getPrice())
                    .totalPrice(totalPrice)
                    .build();
        }




    }

