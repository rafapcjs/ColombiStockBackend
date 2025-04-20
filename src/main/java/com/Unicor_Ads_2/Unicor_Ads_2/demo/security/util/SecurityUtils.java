package com.Unicor_Ads_2.Unicor_Ads_2.demo.security.util;

 import org.springframework.security.access.AccessDeniedException;
 import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUsername() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("No estás autenticado");
        }
        return authentication.getName(); // retorna el username
    }
}

