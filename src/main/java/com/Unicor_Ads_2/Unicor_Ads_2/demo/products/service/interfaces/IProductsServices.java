package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.interfaces;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.CategoryCountProductDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.ProductDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload.ProductPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProductsServices {
    void saveProduct(ProductPayload productPayload);

    void deleteByCode(String code);

    Optional<ProductDTO> findProductsByCodeIgnoreCase(String code);


 void updateProduct (String code , ProductPayload productPayload);
    Page<ProductDTO> findByStockLessThanEqualStockMin(Pageable pageable);

    Page<ProductDTO> findAll(Pageable pageable);

    Page<ProductDTO> findProductsByNameTheCategories(String name, Pageable pageable);

    Page<ProductDTO> findProductsByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);


    List<CategoryCountProductDTO> countProductsByCategory();

}
