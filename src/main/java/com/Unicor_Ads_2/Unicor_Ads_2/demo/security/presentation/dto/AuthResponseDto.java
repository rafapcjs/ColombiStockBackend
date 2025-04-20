package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "message", "status", "jwt"})
public record AuthResponseDto(
        String username,
        String message,
        String jwt,
        Boolean status) {
}
