package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.enums.StatusEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.pageable.PageableUtil;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.interfaces.IStockGetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping(path = EndpointsConstants.ENDPOINT_STOCK_MOVEMENT)
@RequiredArgsConstructor
@Tag(name = "Stock")
public class ControllersGetStockMovement {

    private final IStockGetService stockGetService;

    @Operation(summary = "Obtener todos los stocks",
            description = "Obtiene una lista de todos los stocks, ya sean activos o inactivos de acuerdo al parámetro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de stocks obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Inautorizado - necesitas credenciales"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<StockDTO>> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updateDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam StatusEntity statusEntity) {

        Pageable pageable = PageableUtil.createPageable(page, size, sortBy, direction);
        Page<StockDTO> stockDTOS = stockGetService.findAllStock(pageable, statusEntity);

        return new ResponseEntity<>(stockDTOS, HttpStatus.OK);
    }


    @Operation(summary = "Obtener todos los stocks por tipo de movimiento",
            description = "Obtiene una lista de todos los stocks, ya sean activos o inactivos de acuerdo al parámetro y tipo de movimiento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de stocks obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Inautorizado - necesitas credenciales"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/movement")
    public ResponseEntity<Page<StockDTO>> getAllStocksByMovementType(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updateDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam StatusEntity statusEntity,
            @RequestParam MovementType movementType) {

        Pageable pageable = PageableUtil.createPageable(page, size, sortBy, direction);
        Page<StockDTO> stockDTOS = stockGetService.findAllStockOutOrIn(pageable, statusEntity, movementType);

        return new ResponseEntity<>(stockDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Obtener todos los stocks por rango de fechas",
            description = "Obtiene una lista de todos los stocks, ya sean activos o inactivos de acuerdo al parámetro y rango de fechas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de stocks obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Inautorizado - necesitas credenciales"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/date-range")
    public ResponseEntity<Page<StockDTO>> getAllStocksByDateRange(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updateDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam StatusEntity statusEntity,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") Date endDate) {

        Pageable pageable = PageableUtil.createPageable(page, size, sortBy, direction);
        Page<StockDTO> stockDTOS = stockGetService.findAllByDateBetween(pageable, statusEntity, startDate, endDate);

        return new ResponseEntity<>(stockDTOS, HttpStatus.OK);
    }


    @Operation(summary = "Obtener todos los stocks de hoy",
            description = "Obtiene una lista de todos los stocks creados hoy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de stocks obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Inautorizado - necesitas credenciales"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/today")
    public ResponseEntity<Page<StockDTO>> getAllStocksByTodayDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageableUtil.createPageable(page, size, sortBy, direction);
        Page<StockDTO> stockDTOS = stockGetService.findAllByTodayDate(pageable);

        return new ResponseEntity<>(stockDTOS, HttpStatus.OK);
    }


    @Operation(summary = "Obtener stock por código",
            description = "Obtiene un stock específico por su código y estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock obtenido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "403", description = "Inautorizado - necesitas credenciales"),
            @ApiResponse(responseCode = "404", description = "Stock no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/code")
    public ResponseEntity<StockDTO> getStockByCode(@RequestParam Integer code,
                                                                                                    @RequestParam StatusEntity statusEntity) {

        Optional<StockDTO> stockDTO = stockGetService.findByCode(code, statusEntity);
        return new ResponseEntity<>(stockDTO.get(), HttpStatus.OK);
    }


}
