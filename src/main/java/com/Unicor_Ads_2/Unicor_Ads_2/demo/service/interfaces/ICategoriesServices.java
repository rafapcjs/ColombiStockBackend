package com.Unicor_Ads_2.Unicor_Ads_2.demo.service.interfaces;


import com.Unicor_Ads_2.Unicor_Ads_2.demo.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.dto.CategoriesDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.payload.CategoriesPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ICategoriesServices {

void saveCategory(CategoriesPayload categoriesPayload);

    String  deleteByCode(String code);
    Optional<CategoriesDto> findCategoriesByCodeIgnoreCase( String code);
    Optional<CategoriesDto> findCategoriesByNameIgnoreCase( String name);


    Page<CategoriesDto>findCategorias(int page, int size);


void updateCategory(CategoriesPayload categoriesPayload , String code);
}
