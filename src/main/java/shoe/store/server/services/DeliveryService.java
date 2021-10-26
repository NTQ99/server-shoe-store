package shoe.store.server.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import shoe.store.server.models.Delivery;
import shoe.store.server.models.DeliveryUrl;

@Service("deliveryService")
public class DeliveryService {

    private RestTemplate restTemplate;
    private HttpEntity<?> entityRequest;
    private ParameterizedTypeReference<Map<String, Object>> responseType;

    public static class AddressRequest {
        private int province_id;
        private int district_id;

        public AddressRequest() {
        }

        public int getDistrict_id() {
            return district_id;
        }

        public void setDistrict_id(int district_id) {
            this.district_id = district_id;
        }

        public int getProvince_id() {
            return province_id;
        }

        public void setProvince_id(int province_id) {
            this.province_id = province_id;
        }
    }

    public static class StatusRequest {
        private String order_code;

        public StatusRequest(String order_code) {
            this.order_code = order_code;
        }

        public String getOrder_code() {
            return order_code;
        }

        public void setOrder_code(String order_code) {
            this.order_code = order_code;
        }
    }

    public static class PrintRequest {
        private List<String> order_codes;

        public PrintRequest(List<String> order_codes) {
            this.order_codes = order_codes;
        }

        public List<String> getOrder_codes() {
            return order_codes;
        }

        public void setOrder_codes(List<String> order_codes) {
            this.order_codes = order_codes;
        }
    }

    public void initRequest(String token, String shopId, Object body) {

        restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("token", token);
        headers.set("shopId", shopId);

        entityRequest = new HttpEntity<>(body, headers);
        responseType = new ParameterizedTypeReference<Map<String, Object>>() {};
    }

    public int GHNGetProvinceId(String token, String provinceName) {

        initRequest(token, null, null);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                DeliveryUrl.GHN_DEV_BASE_URL + DeliveryUrl.GHN_GET_PROVINCE, HttpMethod.POST, entityRequest,
                responseType);

        Map<String, Object> map = responseEntity.getBody();
        JSONArray jsonArray = (new JSONObject(map)).getJSONArray("data");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (provinceName.contains(jsonObject.getString("ProvinceName")))
                return Integer.parseInt(jsonObject.optString("ProvinceID"));
        }

        return -1;
    }

    public int GHNGetDistrictId(String token, String provinceName, String districtName) {
        int provinceId = GHNGetProvinceId(token, provinceName);
        if (provinceId == -1)
            return -1;

        AddressRequest body = new AddressRequest();
        body.setProvince_id(provinceId);
        
        initRequest(token, null, body);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                DeliveryUrl.GHN_DEV_BASE_URL + DeliveryUrl.GHN_GET_DISTRICT, HttpMethod.POST, entityRequest, responseType);

        Map<String, Object> map = responseEntity.getBody();
        JSONArray jsonArray = (new JSONObject(map)).getJSONArray("data");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("DistrictName").replace("Thành phố ", "").replace("Thị xã ", "")
                    .replace("Quận ", "").replace("Huyện ", "");
            if (districtName.contains(name))
                return Integer.parseInt(jsonObject.optString("DistrictID"));
        }

        return -1;
    }

    public String GHNGetWardCode(String token, int districtId, String wardName) {

        AddressRequest body = new AddressRequest();
        body.setDistrict_id(districtId);
        
        initRequest(token, null, body);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                DeliveryUrl.GHN_DEV_BASE_URL + DeliveryUrl.GHN_GET_WARD, HttpMethod.POST, entityRequest, responseType);

        Map<String, Object> map = responseEntity.getBody();
        JSONArray jsonArray = (new JSONObject(map)).getJSONArray("data");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("WardName").replace("Xã ", "").replace("Phường ", "")
                    .replace("Thị trấn ", "");
            if (wardName.contains(name))
                return jsonObject.optString("WardCode");
        }

        return "";
    }

    public String GHNCreateOrder(String token, String shopId, Delivery delivery) {

        initRequest(token, shopId, delivery);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
            DeliveryUrl.GHN_DEV_BASE_URL + DeliveryUrl.GHN_CREATE_ORDER, HttpMethod.POST, entityRequest, responseType);

        Map<String, Object> map = responseEntity.getBody();
        JSONObject jsonObject = (new JSONObject(map)).getJSONObject("data");

        return jsonObject.getString("order_code");
    }

    public String GHNGetStatus(String token, String shopId, String orderCode) {

        initRequest(token, shopId, new StatusRequest(orderCode));

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
            DeliveryUrl.GHN_DEV_BASE_URL + DeliveryUrl.GHN_ORDER_DETAIL, HttpMethod.POST, entityRequest, responseType);

        Map<String, Object> map = responseEntity.getBody();
        JSONObject jsonObject = (new JSONObject(map)).getJSONObject("data");

        return jsonObject.getString("status");
    }

    public String GHNGetPrintOrdersLink(String token, List<String> orderCodes) {

        initRequest(token, null, new PrintRequest(orderCodes));

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
            DeliveryUrl.GHN_DEV_BASE_URL + DeliveryUrl.GHN_GET_PRINT_ORDER, HttpMethod.POST, entityRequest, responseType);

        Map<String, Object> map = responseEntity.getBody();
        JSONObject jsonObject = (new JSONObject(map)).getJSONObject("data");

        return DeliveryUrl.GHN_PRINT_ORDER + jsonObject.getString("token");
    }
}
