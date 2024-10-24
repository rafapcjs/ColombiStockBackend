package com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.entity;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock_movements", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")})

public class Stock extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY) // Relación con el Producto
    @JoinColumn(name = "producto_id", nullable = false)
    private Products products;

    private String productCode;

    @Column(unique = true, nullable = false)
    private Integer code;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;


    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime movementDate;

    @Column(name = "description")
    private String description;

}
