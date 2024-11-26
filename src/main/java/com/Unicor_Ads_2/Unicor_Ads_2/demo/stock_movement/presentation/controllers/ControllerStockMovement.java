package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.payload.StockPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.interfaces.IStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping(path = EndpointsConstants.ENDPOINT_STOCK_MOVEMENT)
@RequiredArgsConstructor
public class ControllerStockMovement {

    private final IStockService stockService;

    @Operation(summary = "Crear una entrada de stock",
            description = "Crea una nueva entrada de stock a partir del payload proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada de stock creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/stock-in", headers = "Accept=application/json")
    public ResponseEntity<StockDTO> movementStockIn(@Validated @RequestBody StockPayload stockPayload) throws URISyntaxException {

        StockDTO stockDTO = stockService.movementProductStockIn(stockPayload);

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
    public ResponseEntity<StockDTO> movementStockOut(@Validated @RequestBody StockPayload stockPayload) throws URISyntaxException {

        StockDTO stockDTO = stockService.movementProductStockOut(stockPayload);

        return ResponseEntity.created(new URI(EndpointsConstants.ENDPOINT_STOCK_MOVEMENT)).body(stockDTO);
    }


    @Operation(summary = "Actualizar descripción y cantidad de un movimiento de stock",
            description = "Actualiza la descripción y la cantidad de un movimiento de stock existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento de stock actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Movimiento de stock no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/update-stock", headers = "Accept=application/json")
    public ResponseEntity<Void> updateDescriptionStock(@RequestParam Integer code, @Validated  @RequestBody StockPayload stockPayload)  {
        stockService.updateStock(code, stockPayload);
        return ResponseEntity.ok().build();
    }


}
