package shoe.store.server.models;

import java.util.ArrayList;
import java.util.List;

import shoe.store.server.exceptions.GlobalException;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        
        private String name; // Tên của sản phẩm.
        private String code; // Mã của sản phẩm.
        private int quantity; // Số lượng của sản phẩm.
        private double price; // Giá của sản phẩm.
    }

    private String deliveryUnitName;//  #
    private String to_name; // Tên người nhận hàng. #
    private String to_phone; // Số điện thoại người nhận hàng.  #
    private String to_address; // Địa chỉ Shiper tới giao hàng.
    private String to_ward_code; // Phường của người nhận hàng.
    private int to_district_id; // Quận của người nhận hàng.
    private String client_order_code; // Mã đơn hàng riêng của khách hàng. Giá trị mặc định: null   #
    private int cod_amount; // Tiền thu hộ cho người gửi. Maximum :50.000.000. Giá trị mặc định: 0  #
    private String content; // Nội dung của đơn hàng.   #
    private int weight; // Khối lượng của đơn hàng (gram). Tối đa : 1600000 gram   #
    private int length; // Chiều dài của đơn hàng (cm). Tối đa : 200 cm    #
    private int width; // Chiều rộng của đơn hàng (cm). Tối đa : 200 cm    #
    private int height; // Chiều cao của đơn hàng (cm). Tối đa : 200 cm    #
    private int pick_station_id; // Mã bưu cục để gửi hàng tại điểm. Giá trị mặc định : null
    private int insurance_value; // Giá trị của đơn hàng ( Trường hợp mất hàng , bể hàng sẽ đền theo giá trị của    #
    private String coupon; // Mã giảm giá.
    private int service_type_id; // Mã loại dịch vụ cố định 1:Bay , 2:Đi Bộ , 3:Cồng kềnh. Nếu đã truyền
    private int service_id; // Mã loại dịch vụ.Để lấy thông tin chính xác từng tuyến và thời gian dự kiến
    private int payment_type_id; // Mã người thanh toán phí dịch vụ. 1: Người bán/Người gửi. 2: Người mua/Người
    private String note; // Người gửi ghi chú cho tài xế.   #
    private String required_note; // Ghi chú bắt buộc, Bao gồm: CHOTHUHANG, CHOXEMHANGKHONGTHU, KHONGCHOXEMHANG
    private List<Integer> pick_shift; // Dùng để truyền ca lấy hàng , Sử dụng API Lấy danh sách ca lấy
    private List<Item> items; // Thông tin sản phẩm.    #

    private int shipFee;
    
    public void setRequest(List<Product> products, Order order) {
        List<Item> tmpItems = new ArrayList<Item>();
        int totalWeight = 0;
        for (int i = 0; i < products.size(); i ++) {
            Product product = products.get(i);
            Order.Item productOrder = order.getProducts().get(i);
            Item item = new Item(product.getProductName(), product.getProductCode(), productOrder.getQuantity(), product.getPrice());
            tmpItems.add(item);
            totalWeight += productOrder.getQuantity() * product.getWeight();
        }
        this.setItems(tmpItems);
        this.setWeight(totalWeight);

        this.setTo_name(order.getCustomerName());
        this.setTo_phone(order.getCustomerPhone());
        this.setTo_address(order.getDeliveryTo().getDetail());
        this.setClient_order_code(order.getOrderCode());
        this.setCod_amount(order.getCodAmount());
        this.setInsurance_value(order.getTotalPrice());
        this.setNote(order.getNote());
    }

    public void validateRequest() {
        if (this.getDeliveryUnitName() == null) throw new GlobalException("delivery unit name not null");
        if (this.getRequired_note() == null) throw new GlobalException("required note not null");
    }
}
