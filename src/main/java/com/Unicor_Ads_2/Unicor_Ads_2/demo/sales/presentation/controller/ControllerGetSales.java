package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.controller;

 import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.InvoiceDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.services.interfaces.ISalesServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants.*;

@RestController
@Tag(name = "Sales")
@RequiredArgsConstructor
 public class ControllerGetSales {

    private  final ISalesServices iSalesServices;


    @Operation(summary = "Obtener todas las ventas activas",
            description = "Obtiene una lista de todas las ventas activas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ventas activas obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Inautorizado - necesitas credenciales"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(ENDPOINT_SALES_ACTIVE)
    public ResponseEntity<List<InvoiceDTO>> listActiveSales() {

       List<InvoiceDTO> invoiceDTOS =  iSalesServices.listActiveSales();

        return new ResponseEntity<>(invoiceDTOS, HttpStatus.OK);
    }





    @Operation(summary = "Obtener todas las ventas canceladas",
            description = "Obtiene una lista de todas las ventas canceladas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ventas canceladas obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Inautorizado - necesitas credenciales"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(ENDPOINT_SALES_LIST_CANCLED)
    public ResponseEntity<List<InvoiceDTO>> listCanceledSales() {

        List<InvoiceDTO> invoiceDTOS =  iSalesServices.listActiveSales();

        return new ResponseEntity<>(invoiceDTOS, HttpStatus.OK);
    }

}
