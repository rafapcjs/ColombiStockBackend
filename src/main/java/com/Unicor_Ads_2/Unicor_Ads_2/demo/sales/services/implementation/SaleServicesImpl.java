package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.ResourceNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories.IProductsRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.factory.SalesFactory;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.Sales;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.repositories.ISalesRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.InvoiceDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.InvoiceDetailDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.ProductItemDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.interfaces.ISalesServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SaleServicesImpl
implements ISalesServices {
    private final IProductsRepository iProductsRepository;
    private final ISalesRepository iSalesRepository;
    private final SalesFactory salesFactory;

    @Override
    public InvoiceDTO createSale(List<ProductItemDto> productItems) {


        List<Products> productsForSale = new ArrayList<>();
        List<InvoiceDetailDTO> invoiceDetails = new ArrayList<>();
        double totalAmount = 0.0;


        for (ProductItemDto item : productItems) {

            Products products = iProductsRepository.findProductsByCodeIgnoreCase(item.getCodeProduct()).orElseThrow(() -> new ResourceNotFoundException("Producto not found"));

            if (products.getStock() < item.getQuantity()) {
                throw new ResourceNotFoundException("Stock is so insufficient");

            }

            // update stock current the product
            products.setStock(products.getStock() - item.getQuantity());
            iProductsRepository.save(products);

            double totalPrice = products.getPrice() * item.getQuantity();


            InvoiceDetailDTO detail = salesFactory.createInvoiceDetailDTO(products, totalPrice, item.getQuantity());
            invoiceDetails.add(detail);


            totalAmount += totalPrice;
            productsForSale.add(products);

        }

        Sales sales = Sales.builder()

                .saleDate(new Date())
                .totalAmount(totalAmount)
                .products(productsForSale)
                .build();

        sales = iSalesRepository.save(sales);

        return InvoiceDTO.builder()
                .saleId(sales.getUuid())
                .saleDate(sales.getSaleDate())
                .totalAmount(totalAmount)
                .details(invoiceDetails)
                .build();

    }


    @Override
    public void cancelSale(UUID saleId) {
        // Recuperar la venta por su UUID usando Optional
        Optional<Sales> optionalSale = iSalesRepository.findByUuid(saleId);

        // Si no se encuentra la venta, lanzamos una excepción
        Sales sale = optionalSale.orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        // Verificar si la venta ya ha sido cancelada
        if (sale.isCancelled()) {
            throw new ResourceNotFoundException("Sale has already been cancelled");
        }

        // Verificar si la venta tiene productos asociados
        List<Products> productsSold = sale.getProducts(); // Obtener los productos de la venta
        List<Integer> quantitiesSold = sale.getProductQuantities(); // Obtener las cantidades vendidas

        // Si no hay productos vendidos, lanzar una excepción

        // Iterar sobre los productos vendidos en la venta
        for (int i = 0; i < productsSold.size(); i++) {
            Products product = productsSold.get(i);
            int quantitySold = quantitiesSold.get(i);

            // Restaurar la cantidad vendida al stock
            product.setStock(product.getStock() + quantitySold);  // Asegúrate de sumar la cantidad que se vendió
            iProductsRepository.save(product);  // Guardar el producto con el stock actualizado
        }

        // Marcar la venta como cancelada
        sale.setCancelled(true);
        iSalesRepository.save(sale);  // Guardar la venta con el estado actualizado
    }
}
