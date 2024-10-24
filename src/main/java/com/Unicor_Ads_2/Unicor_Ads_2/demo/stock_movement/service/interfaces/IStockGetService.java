package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.interfaces;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.enums.StatusEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface IStockGetService {
    
    Page<StockDTO> findAllStock(Pageable pageable , StatusEntity statusEntity);

    Page<StockDTO> findAllStockOutOrIn(Pageable pageable , StatusEntity statusEntity , MovementType movementType);

    Page<StockDTO> findAllByDateBetween(Pageable pageable , StatusEntity statusEntity, LocalDate startDate, LocalDate endDate);

    Page<StockDTO> findAllByTodayDate(Pageable pageable );

    Optional<StockDTO> findByCode(Integer code, StatusEntity statusEntity);

}
