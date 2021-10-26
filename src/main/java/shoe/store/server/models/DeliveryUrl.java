package shoe.store.server.models;

public class DeliveryUrl {
    // GHN URL
    public static final String GHN_BASE_URL = "https://online-gateway.ghn.vn";
    public static final String GHN_DEV_BASE_URL = "https://dev-online-gateway.ghn.vn";
    public static final String GHN_CALC_FEE = "/shiip/public-api/v2/shipping-order/fee";
    public static final String GHN_GET_SHOP = "/shiip/public-api/v2/shop/all";
    public static final String GHN_CREATE_SHOP = "/shiip/public-api/v2/shop/register";
    public static final String GHN_CREATE_ORDER = "/shiip/public-api/v2/shipping-order/create";
    public static final String GHN_ORDER_PREVIEW = "/public-api/v2/shipping-order/preview";
    public static final String GHN_GET_PRINT_ORDER = "/shiip/public-api/v2/a5/gen-token";
    public static final String GHN_PRINT_ORDER = "https://dev-online-gateway.ghn.vn/a5/public-api/printA5?token=";
    public static final String GHN_ORDER_DETAIL = "/shiip/public-api/v2/shipping-order/detail";
    public static final String GHN_CANCEL_ORDER = "/shiip/public-api/v2/switch-status/cancel";
    public static final String GHN_GET_SERVICE = "/shiip/public-api/v2/shipping-order/available-services";
    public static final String GHN_GET_PROVINCE = "/shiip/public-api/master-data/province";
    public static final String GHN_GET_DISTRICT = "/shiip/public-api/master-data/district";
    public static final String GHN_GET_WARD = "/shiip/public-api/master-data/ward?district_id";
}
