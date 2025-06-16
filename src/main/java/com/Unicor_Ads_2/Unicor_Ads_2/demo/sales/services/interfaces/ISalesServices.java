package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.interfaces;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.SaleProduct;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.Sales;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISalesServices {

       void cancelSale (UUID saleId);
      List<InvoiceDTO> listActiveSales();
      List<InvoiceDTO> listCancelledSales();
      byte[] createSale(List<ProductItemDto> productItems);
     Page<SaleDTO> getSalesToday(Pageable pageable);


    List<MostSoldProductDTO> getMostSoldProducts();



    ProfitReportDTO getDailyProfit(LocalDate day);

    /**
     * Calcula el reporte de ganancias para la semana ISO que contiene la fecha dada.
     *
     * @param anyDateInWeek Cualquier LocalDate dentro de la semana deseada.
     * @return ProfitReportDTO con detalle semanal.
     */
    ProfitReportDTO getWeeklyProfit(LocalDate anyDateInWeek);

    /**
     * Calcula el reporte de ganancias para el mes indicado.
     *
     * @param yearMonth YearMonth (ej. YearMonth.of(2025, 6) para junio 2025).
     * @return ProfitReportDTO con detalle mensual.
     */
    ProfitReportDTO getMonthlyProfit(YearMonth yearMonth);
}
