package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.entity.Stock;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Products extends BaseEntity {


    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "description", length = 255)
    private String description;
    @Column(name = "price", nullable = false)
    private Double price;
    @Column(name = "purchase_price", nullable = false)
    private Double purchasePrice;

    @Column(name = "stock", nullable = false)
    private int stock; // Cantidad disponible en stock
    @Column(name = "stock_min", nullable = false)
    private int stockMin; // Cantidad mínima de stock
    @Column(name = "unit", length = 20)
    private String unit; // Unidad de medida (e.g., kg, unidades, litros)
    @Column(name = "code", nullable = false, updatable = false, unique = true)
    private String code; // Código único del producto


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;
    @ManyToOne
    @JoinColumn(name = "suppliers_id")
    private Suppliers suppliers;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stockMovements;


}
