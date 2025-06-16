package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthUpdateUserRequest(
        @NotBlank String username,
         @Email String email,        // Validación para el email
        @NotBlank String name,
        @NotBlank String lastName,
        @NotBlank String dni,
        @NotBlank String phone
    ) {
}