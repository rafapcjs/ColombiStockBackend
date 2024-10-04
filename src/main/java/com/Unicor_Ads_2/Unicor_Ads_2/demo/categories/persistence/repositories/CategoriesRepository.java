package com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.repositories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.entities.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, UUID> {
    Optional<Categories> findCategoriesByCodeIgnoreCase(String code);
    Optional<Categories> findCategoriesByNameIgnoreCase(String name);
    Page<Categories> findAll(Pageable pageable);
    void deleteByCode(String code);
}