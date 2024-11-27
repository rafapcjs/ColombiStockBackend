package com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sales")
public class Sales extends BaseEntity {


    @Column(name = "sale_date", nullable = false)
    private Date saleDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    @Column(name = "is_cancelled", nullable = false)
    private boolean isCancelled = false;

    @ElementCollection
    private List<Integer> productQuantities;
    @ManyToMany
    @JoinTable(
            name = "sale_products",
            joinColumns = @JoinColumn(name = "sale_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Products> products; // Lista de productos vendidos en esta venta


}
