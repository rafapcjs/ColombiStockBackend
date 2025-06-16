package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SalesDTO {

    private UUID saleId;  // Identificador único de la venta
    private Date saleDate;  // Fecha de la venta
    private double totalAmount;  // Monto total de la venta
    private List<InvoiceDetailDTO> details;  // Detalles de los productos vendidos (puede ser opcional)

    // Getters y Setters
    public UUID getSaleId() {
        return saleId;
    }

    public void setSaleId(UUID saleId) {
        this.saleId = saleId;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<InvoiceDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<InvoiceDetailDTO> details) {
        this.details = details;
    }
}
