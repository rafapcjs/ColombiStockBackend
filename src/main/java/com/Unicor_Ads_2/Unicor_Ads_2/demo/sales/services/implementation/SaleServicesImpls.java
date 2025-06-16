package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.ResourceNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories.IProductsRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.factory.SalesFactory;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.SaleProduct;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.Sales;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.repositories.ISalesProductRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.repositories.ISalesRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.*;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.interfaces.ISalesServices;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;


import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServicesImpls implements ISalesServices {
    private final IProductsRepository iProductsRepository;
    private final ISalesRepository iSalesRepository;
    private final SalesFactory salesFactory;
    private final ISalesProductRepository iSalesProductRepository;

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");


    @Override
    public Page<SaleDTO> getSalesToday(Pageable pageable) {
        // Obtener la fecha de hoy
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        // Convertir las fechas a Date para la consulta
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

        // Buscar las ventas de hoy con paginación
        Page<Sales> salesPage = iSalesRepository.findBySaleDateGreaterThanEqualAndSaleDateLessThan(startDate, endDate, pageable);

        // Mapeamos las ventas a DTO
        Page<SaleDTO> saleDTOs = salesPage.map(sale -> {
            SaleDTO saleDTO = new SaleDTO();
            saleDTO.setUuid(sale.getUuid());
            saleDTO.setSaleDate(sale.getSaleDate());
            saleDTO.setTotalAmount(sale.getTotalAmount());

            // Mapeamos los productos asociados a la venta
            List<SaleDTO.ProductDTO> productDTOs = sale.getProducts().stream().map(product -> {
                SaleDTO.ProductDTO productDTO = new SaleDTO.ProductDTO();
                productDTO.setUuid(product.getUuid());
                productDTO.setName(product.getName());
                productDTO.setPrice(product.getPrice());
                productDTO.setStock(product.getStock());
                return productDTO;
            }).collect(Collectors.toList());

            saleDTO.setProducts(productDTOs);
            return saleDTO;
        });

        return saleDTOs;

    }
    @Override

    public List<MostSoldProductDTO> getMostSoldProducts() {
        return iSalesProductRepository.findMostSoldProductsForCurrentMonth();
    }

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
            // 1. Inicialización de PDF con márgenes
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4, false);
            document.setMargins(36, 36, 54, 36); // top, right, bottom, left

            // 2. Encabezado: logo + datos de la tienda en tabla de dos columnas
            float[] headerCols = {1f, 3f};
            Table headerTable = new Table(UnitValue.createPercentArray(headerCols))
                    .useAllAvailableWidth()
                    .setMarginBottom(10);
            // Logo
            String logoPath = "src/main/resources/static/Logo-uc.png";
            Image logo = new Image(ImageDataFactory.create(logoPath))
                    .scaleToFit(80, 80)
                    .setMarginRight(10);
            headerTable.addCell(new Cell()
                    .add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Datos de la tienda
            Paragraph storeInfo = new Paragraph()
                    .add(new Text("Tienda ColombiStock\n").setBold().setFontSize(16))
                    .add(new Text("Dirección: Calle 123 #45-67, Montería\n").setFontSize(9))
                    .add(new Text("Tel: 3053283187    Email: colombiStock@gmail.com.com\n").setFontSize(9));
            headerTable.addCell(new Cell()
                    .add(storeInfo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            document.add(headerTable);

            // Línea divisoria
            document.add(new LineSeparator(new SolidLine()).setMarginBottom(10));

            // 3. Datos de la factura
            Paragraph invoiceTitle = new Paragraph("FACTURA DE VENTA")
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8);
            document.add(invoiceTitle);

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1f, 2f}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);
            infoTable.addCell(createCell("Núm. Venta:", TextAlignment.LEFT, false));
            infoTable.addCell(createCell(invoiceDTO.getSaleId().toString(), TextAlignment.LEFT, false));
            infoTable.addCell(createCell("Fecha/Hora:", TextAlignment.LEFT, false));
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(invoiceDTO.getSaleDate());
            infoTable.addCell(createCell(fecha, TextAlignment.LEFT, false));
            document.add(infoTable);

            // 4. Tabla de detalles de productos con estilo
            float[] colWidths = {4f, 1f, 2f, 2f};
            Table table = new Table(UnitValue.createPercentArray(colWidths))
                    .useAllAvailableWidth();

            // Cabecera con fondo gris claro
            Style headerStyle = new Style()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold()
                    .setFontSize(10);
            table.addHeaderCell(new Cell().add(new Paragraph("Producto")).addStyle(headerStyle));
            table.addHeaderCell(new Cell().add(new Paragraph("Cant.")).addStyle(headerStyle).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio U.")).addStyle(headerStyle).setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new Cell().add(new Paragraph("Total")).addStyle(headerStyle).setTextAlignment(TextAlignment.RIGHT));

            // Filas de productos
            for (InvoiceDetailDTO detail : invoiceDTO.getDetails()) {
                table.addCell(createCell(detail.getProductName(), TextAlignment.LEFT, false));
                table.addCell(createCell(String.valueOf(detail.getQuantity()), TextAlignment.CENTER, false));
                table.addCell(createCell(String.format("$%.2f", detail.getUnitPrice()), TextAlignment.RIGHT, false));
                table.addCell(createCell(String.format("$%.2f", detail.getTotalPrice()), TextAlignment.RIGHT, false));
            }

            document.add(table);

            // 5. Bloque de totales con separación y fondo tenue
            document.add(new Paragraph("\n"));
            Table totalTable = new Table(UnitValue.createPercentArray(new float[]{6f, 2f}))
                    .useAllAvailableWidth();
            totalTable.addCell(new Cell()
                    .add(new Paragraph("TOTAL").setBold())
                    .setBorder(Border.NO_BORDER));
            totalTable.addCell(new Cell()
                    .add(new Paragraph(String.format("$%.2f", invoiceDTO.getTotalAmount())).setBold())
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(totalTable);

            // 6. Pie de página con número de página
            int numberOfPages = pdf.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                document.showTextAligned(
                        new Paragraph(String.format("Página %d de %d", i, numberOfPages))
                                .setFontSize(8),
                        550, 20, i,
                        TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
            }

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar factura PDF", e);
        }
    }

    // Helper para crear celdas con estilo uniforme
    private Cell createCell(String text, TextAlignment align, boolean bold) {
        Paragraph p = new Paragraph(text).setFontSize(10);
        if (bold) p.setBold();
        return new Cell().add(p)
                .setPadding(4)
                .setTextAlignment(align)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));
    }

    @Override
    @Transactional
    public void cancelSale(UUID saleId) {
        // 1) Recuperar la venta o lanzar excepción si no existe
        Sales sale = iSalesRepository.findByUuid(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + saleId));

        // 2) Validar que no esté ya cancelada
        if (sale.isCancelled()) {
            throw new IllegalStateException("Sale has already been cancelled: " + saleId);
        }

        // 3) Traer los productos de la venta
        List<SaleProduct> saleProducts = iSalesProductRepository.findBySale(sale);
        if (saleProducts.isEmpty()) {
            throw new IllegalStateException("No products found in this sale: " + saleId);
        }

        // 4) Ajustar stock y guardar cada producto
        for (SaleProduct sp : saleProducts) {
            Products product = sp.getProduct();
            // evitar null en quantity
            int qty = Optional.ofNullable(sp.getQuantity()).orElse(0);
            product.setStock(product.getStock() + qty);
            iProductsRepository.save(product);
        }

        // 5) Marcar la venta como cancelada y persistir
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
    private Date toDateStart(LocalDate ld) {
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /** Convierte LocalDate a Date (23:59:59.999) */
    private Date toDateEnd(LocalDate ld) {
        return Date.from(ld.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }

    /** Daily profit */
    @Override
    public ProfitReportDTO getDailyProfit(LocalDate day) {
        Date start = toDateStart(day);
        Date end   = toDateEnd(day);

        List<Sales> ventasHoy = iSalesRepository.findAllBySaleDateBetweenAndIsCancelledFalse(start, end);

        List<SaleProfitDetailDTO> detalles = ventasHoy.stream()
                .map(this::buildSaleProfitDetail)
                .collect(Collectors.toList());

        double totalRevenue = detalles.stream().mapToDouble(SaleProfitDetailDTO::getTotalRevenue).sum();
        double totalCost    = detalles.stream().mapToDouble(SaleProfitDetailDTO::getTotalCost).sum();
        double totalProfit  = totalRevenue - totalCost;

        String label = day.toString();
        return new ProfitReportDTO(label, detalles, totalRevenue, totalCost, totalProfit);
    }

    /** Weekly profit */
    @Override
    public ProfitReportDTO getWeeklyProfit(LocalDate anyDateInWeek) {
        WeekFields wf = WeekFields.ISO;
        int weekNumber = anyDateInWeek.get(wf.weekOfWeekBasedYear());
        int year       = anyDateInWeek.get(wf.weekBasedYear());

        LocalDate firstDayOfWeek = LocalDate.now()
                .withYear(year)
                .with(wf.weekOfWeekBasedYear(), weekNumber)
                .with(wf.dayOfWeek(), 1);
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        Date start = toDateStart(firstDayOfWeek);
        Date end   = toDateEnd(lastDayOfWeek);

        List<Sales> ventasSemana = iSalesRepository.findAllBySaleDateBetweenAndIsCancelledFalse(start, end);

        List<SaleProfitDetailDTO> detalles = ventasSemana.stream()
                .map(this::buildSaleProfitDetail)
                .collect(Collectors.toList());

        double totalRevenue = detalles.stream().mapToDouble(SaleProfitDetailDTO::getTotalRevenue).sum();
        double totalCost    = detalles.stream().mapToDouble(SaleProfitDetailDTO::getTotalCost).sum();
        double totalProfit  = totalRevenue - totalCost;

        String label = String.format("Semana %d de %d", weekNumber, year);
        return new ProfitReportDTO(label, detalles, totalRevenue, totalCost, totalProfit);
    }

    /** Monthly profit */
    @Override
    public ProfitReportDTO getMonthlyProfit(YearMonth yearMonth) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay  = yearMonth.atEndOfMonth();

        Date start = toDateStart(firstDay);
        Date end   = toDateEnd(lastDay);

        List<Sales> ventasMes = iSalesRepository.findAllBySaleDateBetweenAndIsCancelledFalse(start, end);

        List<SaleProfitDetailDTO> detalles = ventasMes.stream()
                .map(this::buildSaleProfitDetail)
                .collect(Collectors.toList());

        double totalRevenue = detalles.stream().mapToDouble(SaleProfitDetailDTO::getTotalRevenue).sum();
        double totalCost    = detalles.stream().mapToDouble(SaleProfitDetailDTO::getTotalCost).sum();
        double totalProfit  = totalRevenue - totalCost;

        String monthName = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"));
        String label = String.format("%s %d", capitalize(monthName), yearMonth.getYear());
        return new ProfitReportDTO(label, detalles, totalRevenue, totalCost, totalProfit);
    }

    /** Construye el SaleProfitDetailDTO completo para una venta */
    private SaleProfitDetailDTO buildSaleProfitDetail(Sales sale) {
        UUID saleUuid = sale.getUuid();
        Date saleDate   = sale.getSaleDate();

        // Obtenemos todas las líneas de producto de esta venta
        List<SaleProduct> lineas = iSalesProductRepository.findBySale(sale);

        // Calculamos un DTO línea a línea
        List<SaleProductLineDetailDTO> lineDetails = new ArrayList<>();
        double sumRevenue = 0.0;
        double sumCost    = 0.0;

        for (SaleProduct linea : lineas) {
            String code    = linea.getProduct().getCode();
            String name    = linea.getProduct().getName();
            int qty        = linea.getQuantity();
            double unitPr  = linea.getProduct().getPrice();
            double unitCo  = linea.getProduct().getPurchasePrice();

            double revenueLine = unitPr * qty;
            double costLine    = unitCo * qty;
            double profitLine  = revenueLine - costLine;

            sumRevenue += revenueLine;
            sumCost    += costLine;

            SaleProductLineDetailDTO lineDTO = new SaleProductLineDetailDTO(
                    code,
                    name,
                    qty,
                    unitPr,
                    unitCo,
                    revenueLine,
                    costLine,
                    profitLine
            );
            lineDetails.add(lineDTO);
        }

        double totalProfit = sumRevenue - sumCost;
        return new SaleProfitDetailDTO(
                saleUuid,
                saleDate,
                lineDetails,
                sumRevenue,
                sumCost,
                totalProfit
        );
    }

    /** Capitaliza la primera letra */
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}

