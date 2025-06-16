package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopkeeperDto {
    private String username;
    private String email;
    private String dni;
    private String phone;
    private String lastName;
    private boolean isEnabled;
}