package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.ResourceNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories.IProductsRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.factory.SalesFactory;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.SaleProduct;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.Sales;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.repositories.ISalesProductRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.repositories.ISalesRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.InvoiceDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.InvoiceDetailDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.ProductItemDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.interfaces.ISalesServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServicesImpl
        implements ISalesServices {
    private final IProductsRepository iProductsRepository;
    private final ISalesRepository iSalesRepository;
    private final SalesFactory salesFactory;
    private final ISalesProductRepository iSalesProductRepository;

    @Transactional
    public InvoiceDTO createSale(List<ProductItemDto> productItems) {
        List<Products> productsForSale = new ArrayList<>();
        List<InvoiceDetailDTO> invoiceDetails = new ArrayList<>();
        double totalAmount = 0.0;

        Sales sale = new Sales();
        sale.setSaleDate(new Date());

        List<SaleProduct> saleProducts = new ArrayList<>();

        for (ProductItemDto item : productItems) {
            Products product = iProductsRepository.findProductsByCodeIgnoreCase(item.getCodeProduct())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStock() < item.getQuantity()) {
                throw new ResourceNotFoundException("Insufficient stock");
            }

            // Update product stock
            product.setStock(product.getStock() - item.getQuantity());
            iProductsRepository.save(product);

            // Calculate total
            double productTotal = product.getPrice() * item.getQuantity();
            totalAmount += productTotal;

            // Create SaleProduct
            SaleProduct saleProduct = new SaleProduct();
            saleProduct.setSale(sale);
            saleProduct.setProduct(product);
            saleProduct.setQuantity(item.getQuantity()); // Explicitly set quantity

            saleProducts.add(saleProduct);
            productsForSale.add(product);

            // Create invoice detail
            InvoiceDetailDTO detail = salesFactory.createInvoiceDetailDTO(product, productTotal, item.getQuantity());
            invoiceDetails.add(detail);
        }

        // Set sale details
        sale.setTotalAmount(totalAmount);

        // Save sale
        sale = iSalesRepository.save(sale);

        // Save sale products
        iSalesProductRepository.saveAll(saleProducts);

        return InvoiceDTO.builder()
                .saleId(sale.getUuid())
                .saleDate(sale.getSaleDate())
                .totalAmount(totalAmount)
                .details(invoiceDetails)
                .build();
    }

    @Override
    @Transactional
    public void cancelSale(UUID saleId) {
        Sales sale = iSalesRepository.findByUuid(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        if (sale.isCancelled()) {
            throw new IllegalStateException("Sale has already been cancelled");
        }

        List<SaleProduct> saleProducts = iSalesProductRepository.findBySale(sale);

        if (saleProducts.isEmpty()) {
            throw new IllegalStateException("No products found in this sale");
        }

        for (SaleProduct saleProduct : saleProducts) {
            // Add null check and default value
            Products product = saleProduct.getProduct();
            int quantitySold = saleProduct.getQuantity();
            product.setStock(product.getStock() + quantitySold);
            iProductsRepository.save(product);
        }

        sale.setCancelled(true);
        iSalesRepository.save(sale);
    }

    @Override
    public List<InvoiceDTO> listActiveSales() {
        List<Sales> activeSales = iSalesRepository.findByIsCancelledFalse();

        return activeSales.stream()
                .map(sale -> {
                    // Find associated SaleProducts
                    List<SaleProduct> saleProducts = iSalesProductRepository.findBySale(sale);

                    List<InvoiceDetailDTO> details = saleProducts.stream()
                            .map(sp -> {
                                int quantity = sp.getQuantity() != null ? sp.getQuantity() : 0;
                                double totalPrice = sp.getProduct().getPrice() * quantity;
                                return salesFactory.createInvoiceDetailDTO(
                                        sp.getProduct(),
                                        totalPrice,
                                        quantity
                                );
                            })
                            .collect(Collectors.toList());

                    return salesFactory.createInvoiceDTO(sale, details);

                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> listCancelledSales() {
        List<Sales> cancelledSales = iSalesRepository.findByIsCancelledTrue();

        return cancelledSales.stream()
                .map(sale -> {
                    // Find associated SaleProducts
                    List<SaleProduct> saleProducts = iSalesProductRepository.findBySale(sale);

                    List<InvoiceDetailDTO> details = saleProducts.stream()
                            .map(sp -> salesFactory.createInvoiceDetailDTO(
                                    sp.getProduct(),
                                    sp.getProduct().getPrice() * sp.getQuantity(),
                                    sp.getQuantity()
                            ))
                            .collect(Collectors.toList());

                    return salesFactory.createInvoiceDTO(sale, details);

                })
                .collect(Collectors.toList());
    }
}



