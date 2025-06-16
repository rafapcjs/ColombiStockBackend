package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.repositories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.SaleProduct;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.Sales;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.MostSoldProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISalesProductRepository extends JpaRepository<SaleProduct, Long> {
    List<SaleProduct> findBySale(Sales sale);
    Page<SaleProduct> findAll(Pageable pageable);
    void deleteAllBySale(Sales sale);

    @Query("""
        SELECT COALESCE(
            SUM(
                (sp.product.price - sp.product.purchasePrice) * sp.quantity
            ), 
            0
        )
        FROM SaleProduct sp
        WHERE sp.sale.isCancelled = false
          AND sp.sale.saleDate >= :from
          AND sp.sale.saleDate < :to
        """)
    Double calculateProfitBetween(
            @Param("from") Date from,
            @Param("to")   Date to
    );

    @Query("SELECT new com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.presentation.dto.MostSoldProductDTO(sp.product.name, sp.product.code, SUM(sp.quantity)) " +
            "FROM SaleProduct sp " +
            "JOIN sp.sale s " +  // Unimos con la entidad Sales
            "WHERE MONTH(s.saleDate) = MONTH(CURRENT_DATE) AND YEAR(s.saleDate) = YEAR(CURRENT_DATE) " +
            "GROUP BY sp.product " +
            "ORDER BY SUM(sp.quantity) DESC")
    List<MostSoldProductDTO> findMostSoldProductsForCurrentMonth();
}


