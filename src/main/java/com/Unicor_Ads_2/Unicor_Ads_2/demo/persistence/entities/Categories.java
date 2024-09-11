package com.Unicor_Ads_2.Unicor_Ads_2.demo.persistence.entities;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
//https://chatgpt.com/share/26082226-1fa1-478d-aa25-8027c5eb5ce0
public class Categories extends BaseEntity {

@Column(name = "name", length = 50, nullable = false,unique = true)
private String name;

@Column(name = "description", nullable = true)
private  String description;

 @Column(name = "code", nullable = false, updatable = false, unique = true)
private String code;
}
