package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.repositories.CategoriesRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.EntityNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.IntegridadReferencialException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.ProductNotFoundException;
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
    public void saveProduct(ProductPayload productPayload) {
        try {
            // Mapea el payload a la entidad Products
            Products products = modelMapperConfig.modelMapper().map(productPayload, Products.class);

            // Verifica si el payload contiene un código de categoría válido
            if (productPayload.getCodigoCategoria() != null && !productPayload.getCodigoCategoria().isEmpty()) {
                // Busca la categoría por su código
                Optional<Categories> optionalCategory = categoriesRepository.findCategoriesByCodeIgnoreCase(productPayload.getCodigoCategoria());
                if (optionalCategory.isPresent()) {
                    // Asigna la entidad categoría encontrada a products
                    Categories existingCategory = optionalCategory.orElseThrow(); // Obtiene la instancia gestionada directamente
                    products.setCategory(existingCategory); // Asigna la categoría a products
                } else {
                    throw new IllegalArgumentException("Categoría no encontrada con el código proporcionado");
                }
            } else {
                throw new IllegalArgumentException("El producto debe tener un código de categoría");
            }

            // Verifica si el payload contiene un DNI de proveedor válido
            if (productPayload.getDni_provedor() != null && !productPayload.getDni_provedor().isEmpty()) {
                // Busca el proveedor por su DNI
                Optional<Suppliers> optionalSupplier = suppliersRepository.findSuppliersByDniIgnoreCase(productPayload.getDni_provedor());
                if (optionalSupplier.isPresent()) {
                    // Asigna la entidad proveedor encontrada a products
                    Suppliers existingSupplier = optionalSupplier.orElseThrow(); // Obtiene la instancia gestionada directamente
                    products.setSuppliers(existingSupplier); // Asigna el proveedor a products
                } else {
                    throw new IllegalArgumentException("Proveedor no encontrado con el DNI proporcionado");
                }
            } else {
                throw new IllegalArgumentException("El producto debe tener un DNI de proveedor");
            }

            // Guarda el producto
            iProductsRepository.save(products);
        } catch (Exception e) {
            // Maneja la excepción
            throw new UnsupportedOperationException("Error al guardar el producto", e);
        }
    }

    @Override
    @Transactional
    public void deleteByCode(String code) {
        // Buscar el producto por código
        Optional<Products> existingCode = iProductsRepository.findProductsByCodeIgnoreCase(code);

        if (existingCode.isPresent()) {
            Products product = existingCode.get();

            // Verificar si el producto tiene proveedores o categorías asociadas
            // Ambas listas deben estar vacías para proceder con la eliminación
            if (!product.getSuppliers().getProductsList().isEmpty() || !product.getCategory().getProducts().isEmpty()) {
                throw new IntegridadReferencialException("No se puede eliminar el producto con código " + code + " porque tiene productos asociados a proveedores o categorías.");
            }

            // Si no tiene productos asociados, eliminar el producto
            iProductsRepository.deleteByCode(code);
        } else {
            // Si el producto no existe, lanzar la excepción
            throw new EntityNotFoundException("El producto con el código " + code + " no existe.");
        }
    }


        @Override
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findProductsByCodeIgnoreCase(String code) {
        Optional<Products> existingCode = iProductsRepository.findProductsByCodeIgnoreCase(code);

        return existingCode.map(products -> productFactory.createProductDTO(products));


    }


    @Override
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
