package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto;

import com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload.AuthCreateRoleRequestPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecoverPasswordDto(

        @Email String email    // Validación para el email
        ) {
}