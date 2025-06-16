package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.entities.Categories;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.sales.persistencie.entities.SaleProduct;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.stock_movement.persistence.entity.Stock;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.suppliers.persistence.entities.Suppliers;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate

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

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Column(name = "stock")
    private int stock;

    @Column(name = "stock_min", nullable = false)
    private int stockMin;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "code", nullable = false, updatable = false, unique = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

    @ManyToOne
    @JoinColumn(name = "suppliers_id")
    private Suppliers suppliers;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stockMovements;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleProduct> saleProducts;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFacturaCompra> detallesFacturaCompra;

    @Column(name = "fecha_fabricacion")
    private LocalDate fechaFabricacion; // Fecha de fabricación del producto

    @Column(name = "vencimiento")
    private LocalDate vencimiento; // Fecha de vencimiento del producto proporcionada por el usuario

    // Este método ya no es necesario, ya que el vencimiento lo ingresa el usuario directamente
    // public void calcularVencimiento() {
    //     if (this.fechaFabricacion != null && this.vencimiento == null) {
    //         // Si no hay vencimiento, calculamos un vencimiento por defecto (si es necesario)
    //         this.vencimiento = this.fechaFabricacion.plusYears(1); // Ejemplo: 1 año de vida útil
    //     }
    // }
}
