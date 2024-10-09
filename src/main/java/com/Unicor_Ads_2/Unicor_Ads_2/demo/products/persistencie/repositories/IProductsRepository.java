package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IProductsRepository extends JpaRepository<Products, UUID> {
    void deleteByCode(String code);

    @Query("SELECT p FROM Products p WHERE p.stock <= p.stockMin ORDER BY p.name ASC")
    Page<Products> findByStockLessThanEqualStockMin(Pageable pageable);


    @Query("SELECT p FROM Products p JOIN p.category c WHERE c.name LIKE %?1%")
    Page<Products> findProductsByNameTheCategories(String name, Pageable pageable);

    @Query("SELECT c.name, COUNT(p) FROM Products p JOIN p.category c GROUP BY c.name")
    List<Object[]> countProductsByCategory();

    @Query("SELECT p from Products p WHERE  p.price BETWEEN ?1 AND ?2")
    Page<Products> findProductsByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);


    Optional<Products> findProductsByCodeIgnoreCase(String code);

    Page<Products> findAll(Pageable pageable);


}
