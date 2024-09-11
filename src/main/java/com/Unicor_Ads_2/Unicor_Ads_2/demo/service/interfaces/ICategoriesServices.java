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

void  deleteCategory(String code);
    Optional<Categories> findCategoriesByCodeIgnoreCase( String code);

    /**
     * Lista las categorías con paginación.
     *
     * @param pageable Información de paginación (número de página y tamaño de página).
     * @return Un objeto Page que contiene la lista de categorías y la información de paginación.
     */
    Page<CategoriesDto> listCategories(Pageable pageable);


void updateCategory(CategoriesPayload categoriesPayload , String code);
}
