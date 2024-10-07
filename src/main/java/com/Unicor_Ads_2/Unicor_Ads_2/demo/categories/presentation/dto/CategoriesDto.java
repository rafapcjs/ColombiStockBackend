package com.Unicor_Ads_2.Unicor_Ads_2.demo.categories.presentation.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder

public record CategoriesDto (
         String name,

          String description,

         String code){

}
