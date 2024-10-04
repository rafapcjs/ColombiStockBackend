package com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.service.interfaces;



import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.dto.CategoriesDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.payload.CategoriesPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ICategoriesServices {

void saveCategory(CategoriesPayload categoriesPayload);

    String  deleteByCode(String code);
    Optional<CategoriesDto> findCategoriesByCodeIgnoreCase( String code);



    Page<CategoriesDto> findCategorias(Pageable pageable);


void updateCategory(CategoriesPayload categoriesPayload , String code);
}
