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
    public static final String ENDPOINT_PRODUCTS_LOW_STOCK = ENDPOINT_PRODUCTS + "/low_stock";

    public static final String ENDPOINT_SUPPLIERS = ENDPOINT_BASE_API + "/suppliers";
    public static final String ENDPOINT_STOCK_MOVEMENT = ENDPOINT_BASE_API + "/stock_movement";
    // Ruta base de ventas
    public static final String ENDPOINT_FACTURACOMPRAS = ENDPOINT_BASE_API + "/facturas-compra";
    public static final String ENDPOINT_SALES_DETAILS = ENDPOINT_BASE_API + "/sales";



    // Constante para crear una venta (POST /api/sales)
    public static final String ENDPOINT_CREATE_SALE   = ENDPOINT_SALES_DETAILS+"/create";

    // Constante para cancelar una venta (DELETE /api/sales/{saleId})
    public static final String ENDPOINT_CANCEL_SALE   = ENDPOINT_SALES_DETAILS +"/canceled";
     public static final String ENDPOINT_SALES_ACTIVE    = ENDPOINT_SALES_DETAILS+"/active";
    public static final String ENDPOINT_SALES_LIST_CANCLED   = ENDPOINT_SALES_DETAILS+"/listCanceledSales";

    // Endpoints de seguridad
    public static final String ENDPOINT_AUTH_BASE = ENDPOINT_BASE_API + "/auth"; // Base de autenticación
    public static final String ENDPOINT_SIGN_UP = ENDPOINT_AUTH_BASE + "/sign-up";
    public static final String ENDPOINT_CLOSED_AUTH = ENDPOINT_AUTH_BASE + "/logout";
    public static final String ENDPOINT_CHANGE_PASSWORD_EASY = ENDPOINT_AUTH_BASE + "/change-password_easy";
    public static final String ENDPOINT_GETALL_SHOPKEEPR = ENDPOINT_AUTH_BASE + "/getShopKeepers";
    public static final String ENDPOINT_ACTIVEACCES = ENDPOINT_AUTH_BASE + "/active-accounts";

    public static final String ENDPOINT_LOGIN = ENDPOINT_AUTH_BASE + "/login";
    public static final String ENDPOINT_RECOVER_PASSWORD = ENDPOINT_AUTH_BASE + "/recover-password";
    public static final String ENDPOINT_CHANGE_PASSWORD = ENDPOINT_AUTH_BASE + "/change-password";
    public static final String ENDPOINT_ME              = ENDPOINT_AUTH_BASE + "/me";            // Nuevo: datos del usuario en sesión
    public static final String ENDPOINT_UPDATE_INFO_USER              = ENDPOINT_AUTH_BASE + "/updateInfo-user";            // Nuevo: datos del usuario en sesión

}
