package com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class CategoriesDto {

    private String name;

    private  String description;

    private String code;
}
