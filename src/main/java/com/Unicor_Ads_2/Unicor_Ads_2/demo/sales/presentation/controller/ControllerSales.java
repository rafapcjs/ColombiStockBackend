package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.controller;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.Sales;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.*;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.interfaces.ISalesServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants.*;
@RestController
@Tag(name = "Sales")
@RequiredArgsConstructor
public class ControllerSales {





        private final ISalesServices iSalesServices;




    @GetMapping("/today")
    public ResponseEntity<Page<SaleDTO>> getSalesToday(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        // Configurar el orden y la paginación
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(Sort.Order.desc(sortBy)) : Sort.by(Sort.Order.asc(sortBy));
        Pageable pageable = PageRequest.of(page, size, sort);

        // Obtener las ventas de hoy
        Page<SaleDTO> salesToday = iSalesServices.getSalesToday(pageable);

        return ResponseEntity.ok(salesToday);
    }



    @PostMapping(ENDPOINT_CREATE_SALE)
    @Operation(
            summary = "Crear una venta",
            description = "Permite crear una venta asociando múltiples productos como si fueran parte de un carrito de compras. Valida la existencia de los productos y el stock disponible antes de realizar la venta."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Venta creada exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvoiceDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida o error en los datos enviados.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado o stock insuficiente.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<byte[]> generateInvoice(@RequestBody List<ProductItemDto> productItems) {
        // Llamar al servicio para crear la venta y generar el PDF
        byte[] pdfBytes = iSalesServices.createSale(productItems);

        // Configurar la respuesta HTTP con el archivo PDF
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura.pdf") // Forzar descarga
                .contentType(MediaType.APPLICATION_PDF) // Tipo de contenido
                .body(pdfBytes); // Cuerpo del archivo
    }

    @DeleteMapping(ENDPOINT_CANCEL_SALE + "/{uuid}")
    @Operation(
            summary = "Cancelar una venta",
            description = "Permite cancelar una venta y restaurar el stock de los productos involucrados en ella."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Venta cancelada exitosamente y stock restaurado.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Venta no encontrada o ya ha sido cancelada.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> delete(@PathVariable UUID uuid) {
        // Llama al servicio para cancelar la venta
        this.iSalesServices.cancelSale(uuid);

        // Responde con estado No Content (204) si la operación es exitosa
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/colombi_stock/sales/daily")
    public ProfitReportDTO getDailyProfit(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return iSalesServices.getDailyProfit(date);
    }

    /**
     * ▶ GET /api/profit/weekly?date=YYYY-MM-DD
     * <p>
     * Devuelve el reporte de ganancias para la semana ISO que contiene la fecha dada.
     *
     * @param date Cualquier LocalDate dentro de la semana deseada
     * @return ProfitReportDTO con detalle semanal
     */
    @GetMapping("/api/v1/colombi_stock/sales/weekly")
    public ProfitReportDTO getWeeklyProfit(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return iSalesServices.getWeeklyProfit(date);
    }

    /**
     * ▶ GET /api/profit/monthly?year=YYYY&month=MM
     * <p>
     * Devuelve el reporte de ganancias para el mes indicado.
     *
     * @param year  Año en formato numérico (ej. 2025)
     * @param month Mes en formato numérico (1–12)
     * @return ProfitReportDTO con detalle mensual
     */
    @GetMapping("/api/v1/colombi_stock/sales/monthly")
    public ProfitReportDTO getMonthlyProfit(
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        YearMonth ym = YearMonth.of(year, month);
        return iSalesServices.getMonthlyProfit(ym);
    }

    @GetMapping("/api/v1/colombi_stock/sales/most-sold-products")
    public List<MostSoldProductDTO> getMostSoldProducts() {
        return iSalesServices.getMostSoldProducts();
    }


}