package com.Unicor_Ads_2.Unicor_Ads_2.demo.presentation.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesPayload {
    @NotBlank(message = "Name cannot be blank")

    private String name;

    private  String description;

    @NotBlank(message = "Code cannot be blank")
    @Size(min = 4, message = "Code must be at least 4 characters long")

    private String code;
}
