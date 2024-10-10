package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.interfaces;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.enums.StatusEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IStockGetService {
    
    Page<StockDTO> findAllStockIn(Pageable pageable , StatusEntity statusEntity);

    Page<StockDTO> findAllStockOut(Pageable pageable , StatusEntity statusEntity);

    Page<StockDTO> findAllByDateBetween(Pageable pageable , StatusEntity statusEntity);

    Page<StockDTO> findAllByTodayDate(Pageable pageable );

}
