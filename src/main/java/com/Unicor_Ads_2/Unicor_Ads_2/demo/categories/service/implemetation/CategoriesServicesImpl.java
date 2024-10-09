package com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.service.implemetation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.factory.CategoryFactory;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.repositories.CategoriesRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.dto.CategoriesDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.payload.CategoriesPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.CategoryNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.DuplicateCategoryException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.service.interfaces.ICategoriesServices;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriesServicesImpl implements ICategoriesServices {

    private final CategoriesRepository categoriesRepository;
    private final ModelMapper modelMapper;
    private final CategoryFactory categoryFactory;

    @Override
    @Transactional
    public void saveCategory(CategoriesPayload categoriesPayload) {
        try {
            // Verificar si la categoría ya existe
            ModelMapper modelMapper = new ModelMapper();
            // Mapear y guardar la categoría
            Categories categories = modelMapper.map(categoriesPayload, Categories.class);
            categoriesRepository.save(categories);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error al guardar la categoría");
        }
    }

    @Override
    @Transactional
    public String deleteByCode(String code) {
        Optional<Categories> existingCategory = categoriesRepository.findCategoriesByCodeIgnoreCase(code);
        if (existingCategory.isPresent()) {
            categoriesRepository.deleteByCode(code);
            return "Categoría eliminada con éxito " + code;
        } else {
            throw new UnsupportedOperationException("Categoría no encontrada con el código: " + code);
        }
    }

    @Override
    @Transactional(readOnly = true)

    public Optional<CategoriesDto> findCategoriesByCodeIgnoreCase(String code) {
        return this.categoriesRepository.findCategoriesByCodeIgnoreCase(code)
                .map(categories -> categoryFactory.createCategoryDto(categories));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<CategoriesDto> findCategorias(Pageable pageable) {
        // Obtener la página de categorías desde el repositorio
        Page<Categories> categoriesPage = categoriesRepository.findAll(pageable);

        // Convertir la lista de categorías a una lista de DTOs
        List<CategoriesDto> categoriesDtoList = categoriesPage.stream()
                .map(categories -> categoryFactory.createCategoryDto(categories))
                .collect(Collectors.toList());

        // Retornar una nueva página de DTOs con la información de paginación
        return new PageImpl<>(categoriesDtoList, pageable, categoriesPage.getTotalElements());
    }

    @Override
    public void updateCategory(CategoriesPayload categoriesPayload, String code) {
        Optional<Categories> existingCategory = categoriesRepository.findCategoriesByCodeIgnoreCase(code);
        if (existingCategory.isPresent()) {
            Categories category = existingCategory.get();
            Optional<Categories> duplicateCodeCategory = categoriesRepository.findCategoriesByCodeIgnoreCase(categoriesPayload.getCode());
            if (duplicateCodeCategory.isPresent() && !duplicateCodeCategory.get().equals(category)) {
                throw new DuplicateCategoryException("Category with code " + categoriesPayload.getCode() + " already exists.");
            }
            Optional<Categories> duplicateNameCategory = categoriesRepository.findCategoriesByNameIgnoreCase(categoriesPayload.getName());
            if (duplicateNameCategory.isPresent() && !duplicateNameCategory.get().equals(category)) {
                throw new DuplicateCategoryException("Category with name " + categoriesPayload.getName() + " already exists.");
            }
            category.setCode(categoriesPayload.getCode());
            category.setName(categoriesPayload.getName());
            category.setDescription(categoriesPayload.getDescription());
            categoriesRepository.save(category);
        } else {
            throw new CategoryNotFoundException("Category with code " + code + " not found.");
        }
    }
}