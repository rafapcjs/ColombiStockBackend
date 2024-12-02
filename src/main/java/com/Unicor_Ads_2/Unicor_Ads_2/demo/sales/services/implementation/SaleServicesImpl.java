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
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;


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


    public byte[] createSale(List<ProductItemDto> productItems) {
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
            saleProduct.setQuantity(item.getQuantity());

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

        // Create InvoiceDTO
        InvoiceDTO invoiceDTO = InvoiceDTO.builder()
                .saleId(sale.getUuid())
                .saleDate(sale.getSaleDate())
                .totalAmount(totalAmount)
                .details(invoiceDetails)
                .build();

        // Generate PDF and get its bytes
        return generateInvoicePDF(invoiceDTO);
    }

    private byte[] generateInvoicePDF(InvoiceDTO invoiceDTO) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            // Ruta del logo de la universidad
            String logoPath = "src/main/resources/static/Logo-uc.png";

            // Agregar el logo
            Image logo = new Image(ImageDataFactory.create(logoPath));
            logo.setWidth(100); // Ajustar tamaño del logo
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER); // Centrar logo
            document.add(logo);

            // Encabezado principal
            document.add(new Paragraph("Tienda ColombiStock")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            // Encabezado principal
            document.add(new Paragraph("Asignatura : Analisis y diseño de sistemas informatico 2")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Trabajo de Ingeniería de Sistemas - UNICORDOBA")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n")); // Espacio

            // Información de la factura
            document.add(new Paragraph("FACTURA DE VENTA")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Número de Venta: " + invoiceDTO.getSaleId()));
            document.add(new Paragraph("Fecha: " +
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(invoiceDTO.getSaleDate())));
            document.add(new Paragraph("\n")); // Espacio

            // Crear tabla para productos
            float[] columnWidths = {40f, 20f, 20f, 20f}; // Porcentajes que suman 100%
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100)); // Asegurar ancho completo

            // Encabezado de la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Cantidad").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio Unitario").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

            // Detalles de productos
            for (InvoiceDetailDTO detail : invoiceDTO.getDetails()) {
                table.addCell(new Cell().add(new Paragraph(detail.getProductName())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", detail.getUnitPrice()))));
                table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", detail.getTotalPrice()))));
            }

            // Agregar tabla al documento
            document.add(table);

            // Total de la venta
            document.add(new Paragraph("\n")); // Espacio
            document.add(new Paragraph("Total: $" + String.format("%.2f", invoiceDTO.getTotalAmount()))
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT));

            // Cerrar documento
            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar factura PDF", e);
        }
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



