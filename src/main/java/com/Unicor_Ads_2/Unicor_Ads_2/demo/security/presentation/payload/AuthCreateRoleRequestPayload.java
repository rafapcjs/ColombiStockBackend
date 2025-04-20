package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.presentation.payload;

import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record AuthCreateRoleRequestPayload(
        @Size(max = 3, message = "The user cannot have more than 3 roles") List<String> roleListName) {
}
