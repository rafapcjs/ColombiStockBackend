package com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.controllers;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.dto.CategoriesDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.payload.CategoriesPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.service.interfaces.ICategoriesServices;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants.EndpointsConstants;
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
@RequestMapping(path = EndpointsConstants.ENDPOINT_CATEGORIES)
@RequiredArgsConstructor

public class ControllerCategories {

     private  final  ICategoriesServices iCategoriesServices;

    @PostMapping()
    public ResponseEntity<?> save(@RequestBody CategoriesPayload categoriesPayload) throws URISyntaxException {
        iCategoriesServices.saveCategory(categoriesPayload);
        return ResponseEntity.created(new URI("/api/v1/categories")).build();
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> delete(@PathVariable String code) {
        iCategoriesServices.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> update(@PathVariable String code, @RequestBody CategoriesPayload categoriesPayload) {
        iCategoriesServices.updateCategory(categoriesPayload, code);
        return ResponseEntity.ok().build();
    }


    @GetMapping()
    public ResponseEntity<Page<CategoriesDto>> findCategorias(Pageable pageable) {
        Page<CategoriesDto> categories = iCategoriesServices.findCategorias(pageable);

        return ResponseEntity.ok(categories);
    }
    @GetMapping("/{code}")
    public ResponseEntity<?>getCode(@PathVariable String code){
        Optional<CategoriesDto>categoriesDto= iCategoriesServices.findCategoriesByCodeIgnoreCase(code);

        return new ResponseEntity<>(categoriesDto, HttpStatus.OK);

    }

}
