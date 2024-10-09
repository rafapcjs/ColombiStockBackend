package com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.factory;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.dto.CategoriesDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryFactory {

    public CategoriesDto createCategoryDto(Categories categories) {


        return CategoriesDto.builder()
                .code(categories.getCode())
                .description(categories.getDescription())
                .name(categories.getName())
                .build();


    }
}
