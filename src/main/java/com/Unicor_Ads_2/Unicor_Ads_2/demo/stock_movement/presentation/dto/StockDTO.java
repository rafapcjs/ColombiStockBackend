package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.enums.StatusEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Date;

@Builder
public record StockDTO(

        Integer code,

        String productCode
, String name,
        int quantity,

        MovementType movementType,

        LocalDateTime movementDate,

        String description,

        Date updateDate,

        StatusEntity statusEntity

) {
}
