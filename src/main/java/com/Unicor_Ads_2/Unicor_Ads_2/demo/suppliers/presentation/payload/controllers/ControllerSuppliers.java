package com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.SupplierPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.presentation.payload.dto.SupplierDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.service.interfaces.ISuppliersServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping(path = EndpointsConstants.ENDPOINT_SUPPLIERS)
@RequiredArgsConstructor
public class ControllerSuppliers {
    private final ISuppliersServices iSuppliersServices;

    @PostMapping()
    public ResponseEntity<?> save(@RequestBody SupplierPayload supplierPayload) throws URISyntaxException {
        iSuppliersServices.saveSupplier(supplierPayload);
        return ResponseEntity.created(new URI("/api/v1/suppliers")).build();
    }

    @PutMapping("/{dni}")
    public ResponseEntity<?> update(@PathVariable String dni, @RequestBody SupplierPayload supplierPayload) {
        iSuppliersServices.updateSupplier(supplierPayload, dni);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<?> delete(@PathVariable String dni) {
        iSuppliersServices.delete(dni);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> findSupplier(@PathVariable String email) {
        Optional<SupplierDto> supplierDto = iSuppliersServices.findSuppliersByEmail(email);
        return new ResponseEntity<>(supplierDto, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<SupplierDto>> findSuppliers(Pageable pageable) {

        Page<SupplierDto> suppliers = iSuppliersServices.findAllSuppliers(pageable);
        return ResponseEntity.ok(suppliers);
    }
}
