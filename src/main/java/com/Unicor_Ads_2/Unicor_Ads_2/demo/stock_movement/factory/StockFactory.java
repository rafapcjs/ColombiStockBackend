package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.factory;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.entity.Stock;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.payload.StockPayload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StockFactory {


    public StockDTO  stockDTO(Stock stock){

        return  StockDTO.builder()
                .code(stock.getCode())
                .productCode(stock.getProductCode())
                .quantity(stock.getQuantity())
                .movementType(stock.getMovementType())
                .description(stock.getDescription())
                .movementDate(stock.getMovementDate())
                .updateDate(stock.getUpdateDate())
                .statusEntity(stock.getStatusEntity())
                .build();
    }


    public  Stock convertToStock(StockPayload stockPayload, Products products, Integer maxCode , MovementType movementType) {
        Stock stock = new Stock();
        stock.setQuantity((int) stockPayload.getQuantity());
        stock.setDescription(stockPayload.getDescription());
        stock.setProducts(products);
        stock.setProductCode(stockPayload.getProductCode());
        stock.setCode(maxCode + 1);
        stock.setMovementDate(LocalDateTime.now());
        stock.setMovementType(movementType);
        return stock;
    }

}
