package com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.dto.CategoriesDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.payload.CategoriesPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.service.interfaces.ICategoriesServices;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping(path = EndpointsConstants.ENDPOINT_CATEGORIES)
@RequiredArgsConstructor

public class ControllerCategories {

    private final ICategoriesServices iCategoriesServices;

    @Operation(summary = "Guardar una nueva categoría",
            description = "Crea una nueva categoría en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping()
    public ResponseEntity<?> save(@RequestBody CategoriesPayload categoriesPayload) throws URISyntaxException {
        iCategoriesServices.saveCategory(categoriesPayload);
        return ResponseEntity.created(new URI(EndpointsConstants.ENDPOINT_CATEGORIES)).build();
    }

    @Operation(summary = "Eliminar una categoría",
            description = "Elimina una categoría existente por su código.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{code}")
    public ResponseEntity<?> delete(@PathVariable String code) {
        iCategoriesServices.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar una categoría",
            description = "Actualiza los detalles de una categoría existente por su código.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{code}")
    public ResponseEntity<?> update(@PathVariable String code, @RequestBody CategoriesPayload categoriesPayload) {
        iCategoriesServices.updateCategory(categoriesPayload, code);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtener lista de categorías",
            description = "Devuelve una lista paginada de todas las categorías.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<CategoriesDto>> findCategorias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        Page<CategoriesDto> categories = iCategoriesServices.findCategorias(pageable);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Obtener una categoría por código",
            description = "Devuelve los detalles de una categoría específica por su código.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{code}")
    public ResponseEntity<?> getCode(@PathVariable String code) {
        Optional<CategoriesDto> categoriesDto = iCategoriesServices.findCategoriesByCodeIgnoreCase(code);
        return new ResponseEntity<>(categoriesDto, HttpStatus.OK);
    }
}
