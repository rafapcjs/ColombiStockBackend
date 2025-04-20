package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}