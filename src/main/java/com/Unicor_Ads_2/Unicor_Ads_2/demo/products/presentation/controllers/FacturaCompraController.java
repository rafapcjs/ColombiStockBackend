package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.FacturaCompraDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload.FacturaCompraPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.interfaces.IFacturaCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(EndpointsConstants.ENDPOINT_FACTURACOMPRAS)
@RequiredArgsConstructor
public class FacturaCompraController {

    private final IFacturaCompraService facturaService;

    @PostMapping
    public ResponseEntity<String> registrarFactura(@RequestBody @Valid FacturaCompraPayload payload) {
        facturaService.registrarFactura(payload);
        return ResponseEntity.ok("Factura registrada correctamente");
    }

    @PutMapping("/{numeroFactura}")
    public ResponseEntity<String> editarFactura(
            @PathVariable String numeroFactura,
            @RequestBody @Valid FacturaCompraPayload payload
    ) {
        facturaService.editarFactura(numeroFactura, payload);
        return ResponseEntity.ok("Factura editada correctamente");
    }
    @GetMapping
    public ResponseEntity<Page<FacturaCompraDto>> listarFacturas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                direction.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending()
        );

        Page<FacturaCompraDto> resultado = facturaService.listarFacturas(pageable);
        return ResponseEntity.ok(resultado);
    }

}
