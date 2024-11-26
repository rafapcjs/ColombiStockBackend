package com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.persistence.entities;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import com.Unicor_Ads_2.Unicor_Ads_2.demo.products.persistencie.entities.Products;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Categories extends BaseEntity {

 @Column(name = "name", length = 50, nullable = false, unique = true)
 private String name;

 @Column(name = "description")
 private String description;

 @Column(name = "code", nullable = false, updatable = false, unique = true)
 private String code;

 @OneToMany(mappedBy = "category")
 private List<Products> products;



}