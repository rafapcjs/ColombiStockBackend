package com.Unicor_Ads_2.Unicor_Ads_2.demo.service.implemetation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.persistence.repositories.CategoriesRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.dto.CategoriesDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.payload.CategoriesPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.service.exception.CategoryNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.service.exception.DuplicateCategoryException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.service.exception.ResourceAlreadyExistsException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.service.interfaces.ICategoriesServices;
import jakarta.validation.Valid;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriesServicesImpl implements ICategoriesServices {

    private final CategoriesRepository categoriesRepository;

private  final  ModelMapper modelMapper;

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
            return "Categoría eliminada con éxito  "+ code;
            // Elimina la categoría si existe
        } else {
            throw new UnsupportedOperationException("Categoría no encontrada con el código: " + code);  // Excepción personalizada
        }
    }


    @Override
    public Optional<CategoriesDto> findCategoriesByCodeIgnoreCase(String code) {

        Optional<Categories> existingCategory = this.categoriesRepository.findCategoriesByCodeIgnoreCase(code);

        if (existingCategory.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            Categories categories = existingCategory.get();
            CategoriesDto categoriesDto = modelMapper.map(categories, CategoriesDto.class);
            return Optional.of(categoriesDto); // Devolver el DTO dentro de un Optional
        } else {
            return Optional.empty(); // Devolver Optional vacío si no se encuentra la categoría
        }
    }

    @Override
    public Optional<CategoriesDto> findCategoriesByNameIgnoreCase(String name) {


        Optional<Categories> existingCategory = this.categoriesRepository.findCategoriesByNameIgnoreCase(name);

        if (existingCategory.isPresent()) {

            ModelMapper modelMapper = new ModelMapper();

            Categories categories = existingCategory.get();

            CategoriesDto categoriesDto = modelMapper.map(categories, CategoriesDto.class);

            return Optional.of(categoriesDto);


        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriesDto> findCategorias(int page, int size) {
        // Obtiene la página de categorías desde el repositorio
        Page<Categories> categoriesPage = categoriesRepository.findAll(PageRequest.of(page, size));
        // Mapear Categories a CategoriesDto usando ModelMapper en una sola línea
        return categoriesPage.map(category -> modelMapper.map(category, CategoriesDto.class));
    }







    public void updateCategory(CategoriesPayload categoriesPayload, String code) {
        // Buscar la categoría existente por código
        Optional<Categories> existingCategory = categoriesRepository.findCategoriesByCodeIgnoreCase(code);

        // Si se encuentra la categoría, actualizar sus campos
        if (existingCategory.isPresent()) {
            Categories category = existingCategory.get();

            // Verificar si hay otra categoría con el mismo código (sin contar la existente)
            Optional<Categories> duplicateCodeCategory = categoriesRepository.findCategoriesByCodeIgnoreCase(categoriesPayload.getCode());
            if (duplicateCodeCategory.isPresent() && !duplicateCodeCategory.get().equals(category)) {
                throw new DuplicateCategoryException("Category with code " + categoriesPayload.getCode() + " already exists.");
            }

            // Verificar si hay otra categoría con el mismo nombre (sin contar la existente)
            Optional<Categories> duplicateNameCategory = categoriesRepository.findCategoriesByNameIgnoreCase(categoriesPayload.getName());
            if (duplicateNameCategory.isPresent() && !duplicateNameCategory.get().equals(category)) {
                throw new DuplicateCategoryException("Category with name " + categoriesPayload.getName() + " already exists.");
            }

            // Actualizar campos específicos
            category.setCode(categoriesPayload.getCode());
            category.setName(categoriesPayload.getName());
            category.setDescription(categoriesPayload.getDescription());

            // Guardar la categoría actualizada en el repositorio
            categoriesRepository.save(category);
        } else {
            // Lanzar una excepción si la categoría no se encuentra
            throw new CategoryNotFoundException("Category with code " + code + " not found.");
        }
    }


}
