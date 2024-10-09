package com.Unicor_Ads_2.Unicor_Ads_2.demo.products.presentation.dto;

import lombok.Builder;

@Builder
public record CategoryCountProductDTO(String categoryName,Long countProductsByCategory) {

}
