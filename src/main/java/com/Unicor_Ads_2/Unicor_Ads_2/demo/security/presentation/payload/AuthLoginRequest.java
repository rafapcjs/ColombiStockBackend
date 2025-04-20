package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(@NotBlank String username,
                               @NotBlank String password) {
}
