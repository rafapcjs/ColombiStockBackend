package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload.ProductPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.payload.StockPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.interfaces.IStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping(path = EndpointsConstants.ENDPOINT_STOCK_MOVEMENT)
@RequiredArgsConstructor
public class ControllerStockMovement {

    private  final IStockService stockService;

    @Operation(summary = "Crear una entrada de stock",
            description = "Crea una nueva entrada de stock a partir del payload proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada de stock creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/stock-in", headers = "Accept=application/json")
    public ResponseEntity<StockDTO> movementStockIn(@RequestBody StockPayload stockPayload) throws URISyntaxException {

      StockDTO stockDTO =  stockService.movementProductStockIn(stockPayload);

        return ResponseEntity.created(new URI(EndpointsConstants.ENDPOINT_PRODUCTS)).body(stockDTO);
    }



    
    @Operation(summary = "Crear una salida de stock",
            description = "Crea una nueva salida de stock a partir del payload proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Salida de stock creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/stock-out", headers = "Accept=application/json")
    public ResponseEntity<StockDTO> movementStockOut(@RequestBody StockPayload stockPayload) throws URISyntaxException {

        StockDTO stockDTO = stockService.movementProductStockOut(stockPayload);

        return ResponseEntity.created(new URI(EndpointsConstants.ENDPOINT_STOCK_MOVEMENT)).body(stockDTO);
    }

}
