package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.dto.SupplierDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.SupplierPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.service.interfaces.ISuppliersServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping(path = EndpointsConstants.ENDPOINT_SUPPLIERS)
@RequiredArgsConstructor

@Tag(name = "Suppliers")
public class ControllerSuppliers {
    private final ISuppliersServices iSuppliersServices;


    @Operation(summary = "Crear un nuevo proveedor",
            description = "Crea un nuevo proveedor a partir del payload proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(headers = "Accept=application/json")

    public ResponseEntity<?> save(@Validated @RequestBody SupplierPayload supplierPayload) throws URISyntaxException {
        iSuppliersServices.saveSupplier(supplierPayload);
        return ResponseEntity.created(new URI(EndpointsConstants.ENDPOINT_SUPPLIERS)).build();
    }


    @Operation(summary = "Actualizar un proveedor",
            description = "Actualiza un proveedor existente según su DNI con los datos proporcionados en el payload.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/{dni}", headers = "Accept=application/json")

    public ResponseEntity<?> update(@PathVariable String dni, @Validated @RequestBody SupplierPayload supplierPayload) {
        iSuppliersServices.updateSupplier(supplierPayload, dni);

        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Eliminar un proveedor",
            description = "Elimina un proveedor existente según su DNI.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Proveedor eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping(value = "/{dni}", headers = "Accept=application/json")

    public ResponseEntity<?> delete(@PathVariable String dni) {
        iSuppliersServices.delete(dni);
        return ResponseEntity.noContent().build();
    }



    @Operation(summary = "Obtener un proveedor por email",
            description = "Devuelve los detalles de un proveedor según su email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/{email}", headers = "Accept=application/json")

    public ResponseEntity<?> findSupplier(@PathVariable String email) {
        Optional<SupplierDto> supplierDto = iSuppliersServices.findSuppliersByEmail(email);
        return new ResponseEntity<>(supplierDto, HttpStatus.OK);
    }

    @Operation(summary = "Obtener todos los proveedores",
            description = "Devuelve una lista paginada de todos los proveedores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Parámetros de solicitud inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(headers = "Accept=application/json")
    public ResponseEntity<Page<SupplierDto>> findSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

    
        Page<SupplierDto> suppliers = iSuppliersServices.findAllSuppliers(pageable);
        return ResponseEntity.ok(suppliers);
    }
}
