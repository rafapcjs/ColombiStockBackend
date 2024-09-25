package com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.controllers.categories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.dto.CategoriesDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.payload.CategoriesPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.service.interfaces.ICategoriesServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ControllerCategories {
    private  final ICategoriesServices iCategoriesServices;


    @PostMapping()
    public ResponseEntity<?> save(@RequestBody CategoriesPayload categoriesPayload) throws URISyntaxException {
        this.iCategoriesServices.saveCategory(categoriesPayload);
        return ResponseEntity.created(new URI("/api/v1/categories/".concat(categoriesPayload.getCode())))
                .body("Category created with code: " + categoriesPayload.getCode());
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> delete(@PathVariable String code) {
        this.iCategoriesServices.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> update(@PathVariable String code, @RequestBody CategoriesPayload categoriesPayload) {
        this.iCategoriesServices.updateCategory(categoriesPayload, code);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping()
    public ResponseEntity<Page<CategoriesDto>> listarCategorias(
            @PageableDefault(size = 10) Pageable pageable) {
        // Llamar al servicio para obtener la página de categorías
        Page<CategoriesDto> categorias = this.iCategoriesServices.findCategorias(pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(categorias); // Retornar la respuesta con la lista de categorías
    }

}
