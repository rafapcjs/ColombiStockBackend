package com.Unicor_Ads_2.Unicor_Ads_2.demo.service.implemetation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.persistence.repositories.CategoriesRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.dto.CategoriesDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.payload.CategoriesPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.service.exception.ResourceAlreadyExistsException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.service.interfaces.ICategoriesServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriesServicesImpl implements ICategoriesServices {

    private CategoriesRepository categoriesRepository;


    @Override
    public void saveCategory(@Valid  CategoriesPayload categoriesPayload) {


        Optional<Categories>existingCategory= categoriesRepository.findCategoriesByCodeIgnoreCase(categoriesPayload.getCode());
if (existingCategory.isPresent()){
    throw new ResourceAlreadyExistsException("Code '" + categoriesPayload.getCode() + "' already exists");


}
        try {
            ModelMapper modelMapper = new ModelMapper();
            Categories categories= modelMapper.map(categoriesPayload, Categories.class);

            this.categoriesRepository.save(categories);
        }

        catch (Exception e){

            throw  new UnsupportedOperationException("Error al guardar la categoría");
        }
    }

    @Override
    public void deleteCategory(String code) {


        Optional<Categories> existingCategory = categoriesRepository.findCategoriesByCodeIgnoreCase(code);

        if(existingCategory.isPresent()){


        }
    }

    @Override
    public Optional<Categories> findCategoriesByCodeIgnoreCase(String code) {
        return Optional.empty();
    }

    @Override
    public Page<CategoriesDto> listCategories(Pageable pageable) {
        return null;
    }

    @Override
    public void updateCategory(CategoriesPayload categoriesPayload, String code) {

    }
}
