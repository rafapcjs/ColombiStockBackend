package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.enums.StatusEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.ResourceNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.factory.StockFactory;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.entity.Stock;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.repositories.StockRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.interfaces.IStockGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockGetServiceImpl implements IStockGetService {

    private final StockRepository stockRepository;
    private final StockFactory factory;

    @Override
    public Page<StockDTO> findAllStock(Pageable pageable, StatusEntity statusEntity) {

        try {
            Page<Stock> stocks = stockRepository.findAllByStatusEntity(statusEntity, pageable);

            Page<StockDTO> stockDTOS = stocks.map(factory::stockDTO);

            return stockDTOS;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error getting stocks", e);
        }
    }

    @Override
    public Page<StockDTO> findAllStockOutOrIn(Pageable pageable, StatusEntity statusEntity, MovementType movementType) {

        try {
            Page<Stock> stocks = stockRepository.findAllStockOutOrIn(statusEntity, movementType, pageable);
            Page<StockDTO> stockDTOS = stocks.map(factory::stockDTO);
            return stockDTOS;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error getting stocks", e);
        }
    }

    @Override
    public Page<StockDTO> findAllByDateBetween(Pageable pageable, StatusEntity statusEntity, Date startDate, Date endDate) {

        try {
            Page<Stock> stocks = stockRepository.findAllByDateBetween(statusEntity, startDate, endDate, pageable);
            Page<StockDTO> stockDTOS = stocks.map(factory::stockDTO);
            return stockDTOS;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error getting stocks", e);
        }
    }



    @Override
    public Page<StockDTO> findAllByTodayDate(Pageable pageable) {

        try {
            LocalDate today = LocalDate.now();
            Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Page<Stock> stocks = stockRepository.findAllByTodayDate(todayDate, pageable);
            Page<StockDTO> stockDTOS = stocks.map(factory::stockDTO);

            return stockDTOS;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error getting stocks", e);
        }
    }





    @Override
    @Transactional
    public Optional<StockDTO> findByCode(Integer code, StatusEntity statusEntity) {

        Optional<Stock> optionalStock = stockRepository.findByCodeAndStatusEntity(code, statusEntity);

        if (optionalStock.isPresent()) {

           Stock stock = optionalStock.get();

           StockDTO stockDTO = factory.stockDTO(stock);

           return Optional.of(stockDTO);

        } else {
            throw new ResourceNotFoundException("Stock not found");
        }

    }

}
