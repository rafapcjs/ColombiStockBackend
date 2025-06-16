package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "detalle_factura_compra")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
 public class DetalleFacturaCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private FacturaCompra facturaCompra;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Products producto;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;



    // Getters and setters
}
