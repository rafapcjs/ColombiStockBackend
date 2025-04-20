package com.Unicor_Ads_2.Unicor_Ads_2.demo.commons.utils.constants;

public class EndpointsConstants {

    private EndpointsConstants() {
        throw new IllegalStateException("Utility class");
    }

    // Base de la API
    public static final String ENDPOINT_BASE_API = "/api/v1/colombi_stock";

    // Endpoints específicos
    public static final String ENDPOINT_CATEGORIES = ENDPOINT_BASE_API + "/categories";
    public static final String ENDPOINT_PRODUCTS = ENDPOINT_BASE_API + "/products";
    public static final String ENDPOINT_SUPPLIERS = ENDPOINT_BASE_API + "/suppliers";
    public static final String ENDPOINT_STOCK_MOVEMENT = ENDPOINT_BASE_API + "/stock_movement";
    public static final String ENDPOINT_SALES_DETAILS = ENDPOINT_BASE_API + "/sales";

    // Endpoints de seguridad
    public static final String ENDPOINT_AUTH_BASE = ENDPOINT_BASE_API + "/auth"; // Base de autenticación
    public static final String ENDPOINT_SIGN_UP = ENDPOINT_AUTH_BASE + "/sign-up";
    public static final String ENDPOINT_LOGIN = ENDPOINT_AUTH_BASE + "/login";
    public static final String ENDPOINT_RECOVER_PASSWORD = ENDPOINT_AUTH_BASE + "/recover-password";
    public static final String ENDPOINT_CHANGE_PASSWORD = ENDPOINT_AUTH_BASE + "/change-password";

}
