package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.enums.StatusEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories.IProductsRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.entity.Stock;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.factory.StockFactory;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.repositories.StockRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.dto.StockDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.presentation.payload.StockPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.service.interfaces.IStockService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements IStockService {

    private final StockRepository stockRepository;
    private final IProductsRepository productsRepository;
    private final ModelMapper modelMapper;
    private final StockFactory factory;

    private Stock createMovementStock(StockPayload stockPayload, MovementType movementType) {
        if (stockPayload.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Products products = productsRepository.findProductsByCodeIgnoreCase(stockPayload.getProductCode())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con el código: " + stockPayload.getProductCode()));

        // Buscar si ya existe un stock para este producto
        List<Stock> existingStocks = stockRepository.findByProductCode(products.getCode());

        Stock stock;

        if (!existingStocks.isEmpty()) {
            stock = existingStocks.get(0); // Tomar el primer resultado
            stock.setQuantity(stock.getQuantity() + stockPayload.getQuantity());
        } else {
            Integer maxCode = stockRepository.findMaxCode().orElse(999);
            stock = factory.convertToStock(stockPayload, products, maxCode, movementType);
        }

        stock.setMovementType(movementType);

        return stock;
    }

    @Override
    public StockDTO movementProductStockIn(StockPayload stockPayload, MovementType movementType) {
        // Verificar si el tipo de movimiento es de entrada de stock
        if (movementType == MovementType.STOCK_IN) {
            // Crear el movimiento de stock
            Stock stockMovement = createMovementStock(stockPayload, movementType);

            // Actualizar el stock del producto
            Products product = stockMovement.getProducts();
            product.setStock(product.getStock() + stockPayload.getQuantity());

            stockRepository.save(stockMovement); // Guardar el movimiento de stock
            productsRepository.save(product); // Actualizar el producto

            StockDTO stockDTO = factory.stockDTO(stockMovement);
            return stockDTO;

        } else {
            throw new UnsupportedOperationException("Error creating a stock In");
        }
    }

    @Override
    public StockDTO movementProductStockOut(StockPayload stockPayload, MovementType movementType) {
        if (movementType == MovementType.STOCK_OUT) {
            Stock stockMovement = createMovementStock(stockPayload, movementType);
            Products products = stockMovement.getProducts();

            // Verificar que haya suficiente stock para realizar la salida
            if (products.getStock() < stockPayload.getQuantity()) {
                throw new IllegalArgumentException("No hay suficiente stock disponible");
            }

            // Actualizar el stock del producto
            products.setStock(products.getStock() - stockPayload.getQuantity());

            stockRepository.save(stockMovement); // Guardar el movimiento de stock
            productsRepository.save(products); // Actualizar el producto

            StockDTO stockDTO = factory.stockDTO(stockMovement);
            return stockDTO;

        } else {
            throw new UnsupportedOperationException("Error creating a stock Out");
        }
    }




    @Override
    public void updateDescriptionStock(Integer code, StockPayload stockPayload) {

        Optional<Stock> optionalStock = stockRepository.findByCode(code);

        if (optionalStock.isPresent()) {

            Stock currentStock = optionalStock.get();

            modelMapper.map(stockPayload, currentStock);

            currentStock.setDescription(stockPayload.getDescription());

            stockRepository.save(currentStock);

        } else {
            throw new IllegalArgumentException("Stock not found for code ");
        }
    }

    @Override
    public void deleteStockMovement(Integer code) {

    }


    @Scheduled(cron = "0 0 0 1 */3 *")
    public void archiveAfterTime() {
        StatusEntity statusEntity = StatusEntity.ACTIVE;
        int page = 0;
        // Puedes ajustar el tamaño de la página según tus necesidades
        int size = 50;
        Pageable pageable = PageRequest.of(page, size);

        Page<Stock> stocksPage;

        do {
            stocksPage = stockRepository.findAllByStatusEntity(statusEntity, pageable);

            LocalDate currentDate = LocalDate.now();

            for (Stock stock : stocksPage) {
                LocalDate lastUpdateDate = stock.getUpdateDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                long monthsBetween = ChronoUnit.MONTHS.between(lastUpdateDate, currentDate);

                if (monthsBetween >= 3) {
                    stock.setStatusEntity(StatusEntity.ARCHIVED);
                    stockRepository.save(stock);
                }
            }

            pageable = stocksPage.hasNext() ? stocksPage.nextPageable() : null;
        } while (pageable != null);
    }




}
