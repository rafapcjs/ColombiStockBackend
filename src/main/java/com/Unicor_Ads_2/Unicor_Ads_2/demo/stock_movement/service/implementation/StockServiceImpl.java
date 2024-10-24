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
        Products products = productsRepository.findProductsByCodeIgnoreCase(stockPayload.getProductCode())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con el código: " + stockPayload.getProductCode()));
        // Crear un nuevo stock
        Integer maxCode = stockRepository.findMaxCode().orElse(999);
        Stock stock = factory.convertToStock(stockPayload, products, maxCode, movementType);

        stock.setMovementType(movementType);
        return stock;
    }

    private void adjustStockQuantity(Stock stock, StockPayload stockPayload, MovementType movementType) {
        int quantity = stockPayload.getQuantity();
        if (movementType == MovementType.STOCK_IN) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("La cantidad de entrada debe ser mayor que cero");
            }
            stock.setQuantity(stock.getQuantity() + quantity);
        } else if (movementType == MovementType.STOCK_OUT) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("La cantidad de salida debe ser mayor que cero");
            }
            if (stock.getQuantity() < quantity) {
                throw new IllegalArgumentException("No hay suficiente stock disponible");
            }
            stock.setQuantity(stock.getQuantity() - quantity);
        }
    }

    @Override
    public StockDTO movementProductStockIn(StockPayload stockPayload) {
        try {
            // Buscar el stock existente del producto
            Products product = productsRepository.findProductsByCodeIgnoreCase(stockPayload.getProductCode())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con el código: " + stockPayload.getProductCode()));

            // Actualizar la cantidad del stock existente
            product.setStock(product.getStock() + stockPayload.getQuantity());
            productsRepository.save(product);

            // Crear un nuevo registro de movimiento de stock
            Stock stockMovement = createMovementStock(stockPayload, MovementType.STOCK_IN);
            stockRepository.save(stockMovement);

            return factory.stockDTO(stockMovement);

        } catch (Exception e) {
            throw new UnsupportedOperationException("Error creating stock movement IN", e);
        }
    }


    @Override
    public StockDTO movementProductStockOut(StockPayload stockPayload) {
        try {
            // Buscar el producto correspondiente
            Products product = productsRepository.findProductsByCodeIgnoreCase(stockPayload.getProductCode())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con el código: " + stockPayload.getProductCode()));

            // Verificar si hay suficiente stock disponible
            if (product.getStock() < stockPayload.getQuantity()) {
                throw new IllegalArgumentException("No hay suficiente stock disponible");
            }

            // Reducir la cantidad del stock del producto
            product.setStock(product.getStock() - stockPayload.getQuantity());
            productsRepository.save(product);

            // Crear un nuevo registro de salida de stock
            Stock stockOut = createMovementStock(stockPayload, MovementType.STOCK_OUT);
            stockRepository.save(stockOut);

            return factory.stockDTO(stockOut);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("Error creating stock movement OUT", e);
        }
    }


    @Override
    public void updateStock(Integer code, StockPayload stockPayload) {

        Optional<Stock> optionalStock = stockRepository.findByCode(code);

        if (optionalStock.isPresent()) {
            Stock currentStock = optionalStock.get();
            Products product = currentStock.getProducts();

            int originalQuantity = currentStock.getQuantity();
            int newQuantity = stockPayload.getQuantity();

            if (currentStock.getMovementType() == MovementType.STOCK_IN) {


                product.setStock(product.getStock() - originalQuantity + newQuantity);
            } else if (currentStock.getMovementType() == MovementType.STOCK_OUT) {


                if (product.getStock() + originalQuantity < newQuantity) {
                    throw new IllegalArgumentException("No hay suficiente stock disponible para actualizar");
                }

                product.setStock(product.getStock() + originalQuantity - newQuantity);
            }

            productsRepository.save(product);

            currentStock.setDescription(stockPayload.getDescription());
            currentStock.setQuantity(newQuantity);

            stockRepository.save(currentStock);

        } else {
            throw new IllegalArgumentException("Stock not found for code " + code);
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
