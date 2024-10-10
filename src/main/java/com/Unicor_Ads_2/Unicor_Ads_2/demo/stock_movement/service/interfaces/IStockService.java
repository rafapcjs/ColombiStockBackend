package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.interfaces;


import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.payload.StockPayload;

import java.util.Optional;


public interface IStockService {


    StockDTO movementProductStockIn(StockPayload stockPayload, MovementType movementType);

     StockDTO movementProductStockOut(StockPayload stockPayload, MovementType movementType);

    void updateDescriptionStock(Integer code , StockPayload stockPayload);

    void deleteStockMovement(Integer code);

    void archiveAfterTime();



}
