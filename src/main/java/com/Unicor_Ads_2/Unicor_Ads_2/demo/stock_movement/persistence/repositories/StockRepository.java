package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.repositories;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.enums.StatusEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.entity.Stock;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock , UUID> {

    Optional<Stock> findByCodeAndStatusEntity(Integer code, StatusEntity statusEntity );

    Optional<Stock> findFirstByProductCodeAndMovementTypeOrderByUpdateDateDesc(String productCode, MovementType movementType);

    Optional<Stock> findByCode(Integer code);

    Page<Stock> findAllByStatusEntity(StatusEntity statusEntity , Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE s.statusEntity = :statusEntity AND s.movementType = :movementType")
    Page<Stock> findAllStockOutOrIn(@Param("statusEntity") StatusEntity statusEntity, @Param("movementType") MovementType movementType, Pageable pageable);

    @Query("SELECT MAX(sm.code) FROM Stock sm")
    Optional<Integer> findMaxCode();

    @Query("SELECT s FROM Stock s WHERE s.updateDate = :today")
    Page<Stock> findAllByTodayDate(@Param("today") LocalDate today, Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE s.statusEntity = :statusEntity AND s.updateDate BETWEEN :startDate AND :endDate")
    Page<Stock> findAllByDateBetween(@Param("statusEntity") StatusEntity statusEntity, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);


}
