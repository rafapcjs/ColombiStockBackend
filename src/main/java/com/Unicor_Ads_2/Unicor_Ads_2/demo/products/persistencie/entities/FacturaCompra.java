package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
 @Setter
@Getter

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facturas_compra")
public class FacturaCompra extends BaseEntity {
    @Column(name = "numero_factura", nullable = false, unique = true)
    private String numeroFactura;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Suppliers proveedor;

    @OneToMany(mappedBy = "facturaCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFacturaCompra> productosComprados;
}
