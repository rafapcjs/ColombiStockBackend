package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.repositories.CategoriesRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.*;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.configurations.ModelMapperConfig;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.factory.ProductFactory;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories.IProductsRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.CategoryCountProductDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.ProductDTO;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload.ProductPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.interfaces.IProductsServices;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.repostories.SuppliersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductcServicesImpl implements IProductsServices {

    private final IProductsRepository iProductsRepository;
    private final ModelMapperConfig modelMapperConfig;
    private final SuppliersRepository suppliersRepository;
    private final CategoriesRepository categoriesRepository;
    private  final ProductFactory productFactory;

@Override
@Transactional

    public long countProductsWithLowStock() {
        // Obtener los productos cuyo stock es menor que el stock mínimo
        List<Products> lowStockProducts = iProductsRepository.findProductsWithLowStock();
        return lowStockProducts.size(); // Retorna la cantidad de productos con bajo stock
    }
    @Override
    @Transactional(readOnly = true)
    public Long getTotalStock() {
        return iProductsRepository.sumTotalStock();
    }
    @Override
    @Transactional
    public void saveProduct(ProductPayload productPayload) {
        // Mapea el payload a la entidad Products
        Products products = modelMapperConfig.modelMapper().map(productPayload, Products.class);

        // Asigna la categoría por código
        Categories category = categoriesRepository.findCategoriesByCodeIgnoreCase(productPayload.getCodigoCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el código proporcionado"));
        products.setCategory(category);

        // Asigna el proveedor por DNI
        Suppliers supplier = suppliersRepository.findSuppliersByDniIgnoreCase(productPayload.getDni_provedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con el DNI proporcionado"));
        products.setSuppliers(supplier);

        // El precio de compra y el stock provienen de otros sistemas (no se obtienen de la factura aquí)
        // El precio de compra y stock real ya están asignados automáticamente en el sistema, así que no los tomamos de la factura

        // Asignar el precio de venta proporcionado por el usuario
        products.setPrice(productPayload.getPrice());

        // Asignar el stock mínimo proporcionado por el usuario
        products.setStockMin(productPayload.getStockMin());

        // Asignar la fecha de vencimiento proporcionada por el usuario
        if (productPayload.getVencimiento() != null) {
            // Validación si la fecha de vencimiento es en el futuro
            if (productPayload.getVencimiento().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha actual");
            }
            products.setVencimiento(productPayload.getVencimiento());
        }

        // Guarda el producto con los valores proporcionados por el usuario
        iProductsRepository.save(products);
    }



    @Override
    @Transactional
    public void deleteByCode(String code) {
        // Buscar el producto por código
        Optional<Products> existingCode = iProductsRepository.findProductsByCodeIgnoreCase(code);

        // Verificar si el producto existe
        if (existingCode.isPresent()) {
            Products product = existingCode.get();

            // Verificar si el producto no tiene movimientos de stock ni ventas asociadas
            if (product.getStockMovements().isEmpty() && product.getSaleProducts().isEmpty()) {
                // Si no tiene productos asociados, eliminar el producto
                iProductsRepository.deleteByCode(code);
            } else {
                // Si el producto tiene asociaciones, lanzar excepción
                throw new IntegridadReferencialException("El producto con el código " + code + " tiene movimientos de stock o ventas asociadas, no se puede eliminar.");
            }
        } else {
            // Si no se encuentra el producto, lanzar excepción de producto no encontrado
            throw new ResourceNotFoundException("El producto con el código " + code + " no existe.");
        }
    }



    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findProductsByCodeIgnoreCase(String code) {
        Optional<Products> existingCode = iProductsRepository.findProductsByCodeIgnoreCase(code);

        return existingCode.map(products -> productFactory.createProductDTO(products));

    }

    @Override
    public void updateProduct(String code, ProductPayload productPayload) {
        // Buscar el producto por el código proporcionado
        Optional<Products> optionalProducts = iProductsRepository.findProductsByCodeIgnoreCase(code);

        if (optionalProducts.isPresent()) {
            Products products = optionalProducts.get();

            // Actualizar solo los campos que se pueden modificar
            products.setName(productPayload.getName());
            products.setDescription(productPayload.getDescription());
            products.setStockMin(productPayload.getStockMin()); // El stock mínimo se puede actualizar

            // El precio de venta (price) puede ser actualizado
            if (productPayload.getPrice() != null) {
                products.setPrice(productPayload.getPrice());  // Actualizamos el precio de venta
            }

            // Si la fecha de vencimiento ha sido proporcionada, actualízala
            if (productPayload.getVencimiento() != null) {
                // Validación de la fecha de vencimiento
                if (productPayload.getVencimiento().isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha actual");
                }
                products.setVencimiento(productPayload.getVencimiento());
            }

            // **No se deben actualizar ni el stock ni el precio de compra** (purchasePrice) directamente.
            // El stock y el precio de compra deberían ser gestionados por el sistema y tomados de otros procesos, como la factura.

            // Guarda el producto actualizado en la base de datos
            iProductsRepository.save(products);
        } else {
            throw new ResourceNotFoundException("Producto con código " + code + " no encontrado.");
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findByStockLessThanEqualStockMin(Pageable pageable) {
        // Obtiene la página de productos con stock menor o igual al mínimo
        Page<Products> productsPage = iProductsRepository.findByStockLessThanEqualStockMin(pageable);

        // Convierte la lista de productos a una lista de ProductDTO
        List<ProductDTO> productDTOList = productsPage
                .stream()
                .map(productFactory::createProductDTO) // Método de referencia para simplificar el código
                .collect(Collectors.toList());

        // Devuelve un PageImpl con la lista de ProductDTOs
        return new PageImpl<>(productDTOList, pageable, productsPage.getTotalElements());
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        // Obtiene la página de productos
        Page<Products> productsPage = iProductsRepository.findAll(pageable);

        // Convierte la lista de productos a una lista de ProductDTO
        List<ProductDTO> productDTOList = productsPage.getContent().stream().map(products ->
                productFactory.createProductDTO(products)
        ).collect(Collectors.toList());

        // Devuelve un PageImpl con la lista de ProductDTOs
        return new PageImpl<>(productDTOList, pageable, productsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findProductsByNameTheCategories(String name, Pageable pageable) {
        // Buscar los productos desde el repositorio
        Page<Products> productsPage = iProductsRepository.findProductsByNameTheCategories(name, pageable);

        // Mapear los productos a ProductDTO
        List<ProductDTO> productDTOList = productsPage.getContent().stream()
                .map(products ->
                      productFactory.createProductDTO(products)
                ).collect(Collectors.toList());

        // Retornar la página con ProductDTO
        return new PageImpl<>(productDTOList, pageable, productsPage.getTotalElements());
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findProductsByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        // Llamar al repositorio para obtener los productos con precios entre minPrice y maxPrice
        Page<Products> productsPage = iProductsRepository.findProductsByPriceBetween(minPrice, maxPrice, pageable);

        // Convertir la lista de Products a ProductDTO
        List<ProductDTO> productDTOList = productsPage.getContent().stream().map(products ->
           productFactory.createProductDTO(products)
        ).collect(Collectors.toList());

        // Retornar los resultados paginados en una PageImpl de ProductDTO
        return new PageImpl<>(productDTOList, pageable, productsPage.getTotalElements());
    }

    @Override
    @Transactional()
    public List<CategoryCountProductDTO> countProductsByCategory() {
        // Llamar al método del repositorio que devuelve List<Object[]>
        List<Object[]> results = iProductsRepository.countProductsByCategory();

        // Mapear los resultados a una lista de CategoryCountProductDTO
        return results.stream().map(result -> {
            String categoryName = (String) result[0]; // Primer valor: nombre de la categoría
            Long productCount = ((Number) result[1]).longValue(); // Segundo valor: cantidad de productos

            // Crear y retornar un DTO con estos valores
            return new CategoryCountProductDTO(categoryName, productCount);
        }).collect(Collectors.toList());
    }


}
