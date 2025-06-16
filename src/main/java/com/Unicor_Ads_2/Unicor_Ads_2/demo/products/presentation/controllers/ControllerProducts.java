package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.CategoryCountProductDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.ProductDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload.ProductPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.interfaces.IProductsServices;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants.ENDPOINT_PRODUCTS;
import static com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants.ENDPOINT_PRODUCTS_LOW_STOCK;

@Tag(name = "product")
@RestController
 @RequiredArgsConstructor

public class ControllerProducts {


    private final  IProductsServices iProductsServices;
    // Crear un nuevo producto
    @Operation(summary = "Crear un nuevo producto",
            description = "Crea un nuevo producto a partir del payload proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping( value = ENDPOINT_PRODUCTS , headers = "Accept=application/json")
    public ResponseEntity<?> save(@Validated @RequestBody ProductPayload productPayload) throws URISyntaxException {
        iProductsServices.saveProduct(productPayload);
        return ResponseEntity.created(new URI(EndpointsConstants.ENDPOINT_PRODUCTS)).build();
    }


    // Obtener lista paginada de productos
    @Operation(summary = "Obtener lista de productos",
            description = "Devuelve una lista paginada de todos los productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = ENDPOINT_PRODUCTS  ,headers = "Accept=application/json")
    public ResponseEntity<Page<ProductDTO>> get(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Page<ProductDTO> productDTOS = iProductsServices.findAll(pageable);
        return ResponseEntity.ok(productDTOS);
    }

    // Eliminar un producto por código
    @Operation(summary = "Eliminar un producto",
            description = "Elimina un producto existente según su código.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping(value = "/{code}", headers = "Accept=application/json")
    public ResponseEntity<?> delete(@PathVariable String code) {
        iProductsServices.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }

    // Obtener un producto por código
    @Operation(summary = "Obtener un producto por código",
            description = "Devuelve los detalles de un producto según su código.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/{code}", headers = "Accept=application/json")
    public ResponseEntity<Optional<ProductDTO>> getCode(@PathVariable String code) {
        Optional<ProductDTO> productDto = iProductsServices.findProductsByCodeIgnoreCase(code);
        return ResponseEntity.ok(productDto);
    }

    @Operation(summary = "Obtener productos con bajo stock",
            description = "Devuelve una lista paginada de productos cuyo stock es menor o igual al stock mínimo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de solicitud inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })

    @GetMapping( value = ENDPOINT_PRODUCTS_LOW_STOCK, headers = "Accept=application/json")
    public ResponseEntity<Page<ProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return ResponseEntity.ok(iProductsServices.findByStockLessThanEqualStockMin(pageable));
    }


    // Buscar productos dentro de un rango de precios
    @Operation(summary = "Buscar productos por rango de precios",
            description = "Devuelve una lista de productos cuyo precio está entre un mínimo y un máximo especificados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/findProductsByPriceBetween", headers = "Accept=application/json")
    public ResponseEntity<Page<ProductDTO>> findProductsByPriceBetween(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Page<ProductDTO> productDTOS = iProductsServices.findProductsByPriceBetween(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(productDTOS);
    }

    // Contar productos por categoría
    @Operation(summary = "Contar productos por categoría",
            description = "Devuelve una lista con el número de productos por cada categoría.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/countProductsByCategory", headers = "Accept=application/json")
    public ResponseEntity<List<CategoryCountProductDTO>> countProductsByCategory() {
        List<CategoryCountProductDTO> categoryCountList = iProductsServices.countProductsByCategory();
        return ResponseEntity.ok(categoryCountList);
    }







    @Operation(summary = "Actualizar un producto",
            description = "Actualiza la información de un producto dado su código.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })

@PutMapping(value = ENDPOINT_PRODUCTS +"/{code}" ,headers = "Accept=application/json")

    public ResponseEntity<?> updateProduct(
            @PathVariable("code") String code,
            @RequestBody ProductPayload productPayload) {

         this.iProductsServices.updateProduct(code, productPayload);



             return ResponseEntity.noContent().build();
        }


    @GetMapping(value = ENDPOINT_PRODUCTS+"/count-low-stock")
    public long getLowStockProductCount() {
        return iProductsServices.countProductsWithLowStock(); // Retorna la cantidad de productos con stock bajo
    }

    @GetMapping(value = ENDPOINT_PRODUCTS+"/total-stock")
    public Long getTotalStock() {
        return iProductsServices.getTotalStock();  // Llamamos al servicio para obtener la suma de stock
    }

}
