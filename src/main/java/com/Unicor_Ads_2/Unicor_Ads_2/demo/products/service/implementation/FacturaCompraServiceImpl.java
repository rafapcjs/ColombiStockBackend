package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.implementation;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.exception.ResourceNotFoundException;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.DetalleFacturaCompra;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.FacturaCompra;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories.DetalleFacturaCompraRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories.FacturaCompraRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.repositories.IProductsRepository;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.DetalleProductoDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto.FacturaCompraDto;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload.DetalleProductoPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.payload.FacturaCompraPayload;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.service.interfaces.IFacturaCompraService;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.repostories.SuppliersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

// FacturaCompraServiceImpl.java
@Service
@RequiredArgsConstructor
public class FacturaCompraServiceImpl implements IFacturaCompraService {

    private final FacturaCompraRepository facturaRepo;
    private final DetalleFacturaCompraRepository detalleRepo;
    private final SuppliersRepository suppliersRepo;
    private final IProductsRepository productsRepo;

    @Override
    @Transactional
    public void registrarFactura(FacturaCompraPayload payload) {
        Suppliers proveedor = suppliersRepo.findSuppliersByDniIgnoreCase(payload.proveedorDni())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        // 1. Crear o recuperar FacturaCompra
        FacturaCompra factura = facturaRepo.findByNumeroFactura(payload.numeroFactura())
                .orElseGet(() -> {
                    var nueva = FacturaCompra.builder()
                            .numeroFactura(payload.numeroFactura())
                            .fechaCompra(payload.fechaCompra())
                            .proveedor(proveedor)
                            .build();
                    return facturaRepo.save(nueva);
                });

        // 2. Para cada producto en la factura:
        for (var p : payload.productos()) {
            Products producto = productsRepo.findByCodeIgnoreCase(p.productCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + p.productCode()));

            // 2.a) Actualizar stock
            producto.setStock(producto.getStock() + p.cantidad());
            productsRepo.saveAndFlush(producto);

            // 2.b) Actualizar purchasePrice
            productsRepo.actualizarPrecioCompra(producto.getCode(), p.precioUnitario());

            // 2.c) Crear detalle de factura
            DetalleFacturaCompra detalle = DetalleFacturaCompra.builder()
                    .facturaCompra(factura)
                    .producto(producto)
                    .cantidad(p.cantidad())
                    .precioUnitario(p.precioUnitario())
                    .build();
            detalleRepo.save(detalle);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FacturaCompraDto> listarFacturas(Pageable pageable) {
        Page<FacturaCompra> facturas = facturaRepo.findAll(pageable);

        return facturas.map(factura -> {
            List<DetalleProductoDto> productos = factura.getProductosComprados().stream()
                    .map(detalle -> {
                        // Calculamos el total por producto (cantidad * precio unitario)
                        double totalPorProducto = detalle.getCantidad() * detalle.getPrecioUnitario();

                        // Construimos el detalle del producto con la cantidad, precio y total
                        return DetalleProductoDto.builder()
                                .nombreProducto(detalle.getProducto().getName())
                                .cantidad(detalle.getCantidad())
                                .precioUnitario(detalle.getPrecioUnitario())
                                .totalPorProducto(totalPorProducto)  // Agregamos el total
                                .build();
                    })
                    .collect(Collectors.toList());

            // Calculamos el total general de la factura (sumando todos los productos)
            double totalFactura = productos.stream()
                    .mapToDouble(DetalleProductoDto::getTotalPorProducto)
                    .sum();

            // Retornamos la factura con el total general calculado
            return FacturaCompraDto.builder()
                    .numeroFactura(factura.getNumeroFactura())
                    .proveedorNombre(factura.getProveedor().getName())
                    .fechaCompra(factura.getFechaCompra())
                    .productos(productos)
                    .totalFactura(totalFactura)  // Agregamos el total general de la factura
                    .build();
        });
    }


    @Override
    @Transactional
    public void editarFactura(String numeroFactura, FacturaCompraPayload payload) {
        // 1. Buscar la factura existente
        FacturaCompra factura = facturaRepo.findByNumeroFactura(numeroFactura)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));

        // 2. Revertir stock y eliminar detalles anteriores de forma segura
        Iterator<DetalleFacturaCompra> iterator = factura.getProductosComprados().iterator();
        while (iterator.hasNext()) {
            DetalleFacturaCompra detalle = iterator.next();
            Products producto = detalle.getProducto();

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productsRepo.saveAndFlush(producto);

            iterator.remove(); // elimina del contexto JPA (orphanRemoval lo borrará)
        }

        facturaRepo.save(factura); // aplica orphanRemoval

        // 3. Actualizar proveedor y fecha de la factura
        Suppliers proveedor = suppliersRepo.findSuppliersByDniIgnoreCase(payload.proveedorDni())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        factura.setProveedor(proveedor);
        factura.setFechaCompra(payload.fechaCompra());

        // 4. Agregar nuevos detalles: actualizar stock + UPDATE directo de purchasePrice
        for (var p : payload.productos()) {
            Products producto = productsRepo.findByCodeIgnoreCase(p.productCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + p.productCode()));

            // 4.a) Actualizar stock primero
            producto.setStock(producto.getStock() + p.cantidad());
            productsRepo.saveAndFlush(producto);

            // 4.b) Actualizar precio de compra con JPQL
            productsRepo.actualizarPrecioCompra(producto.getCode(), p.precioUnitario());

            // 4.c) Crear nuevo detalle de factura
            DetalleFacturaCompra detalle = DetalleFacturaCompra.builder()
                    .facturaCompra(factura)
                    .producto(producto)
                    .cantidad(p.cantidad())
                    .precioUnitario(p.precioUnitario())
                    .build();

            factura.getProductosComprados().add(detalle);
        }

        // 5. Guardar factura con los nuevos detalles
        facturaRepo.save(factura);
    }



}
