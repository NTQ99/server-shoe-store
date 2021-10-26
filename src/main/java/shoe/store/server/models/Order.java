package shoe.store.server.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import shoe.store.server.exceptions.GlobalException;

@Document(collection = "order")
@Getter @Setter
@AllArgsConstructor
public class Order {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        private String productId;
        private String productName;
        private int quantity;
    }

    public enum Status {
        wait_confirm, // chờ xác nhận từ người mua
        not_responded, // người mua không phản hồi
        canceled, // hủy đơn
        await_trans, // chờ vận chuyển
        success, // giao thành công
        fail // giao thất bại
    }

    @Id
    private String id;

    private String userId;
    private String orderCode;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private List<Item> products;
    private String deliveryUnitName;
    private String deliveryCode;
    private String note;
    private Address deliveryTo;
    private int shipFee;
    private int totalPrice;
    private int codAmount; // -1: bằng totalPrice
    private boolean isPrinted;
    private Status status;
    private int priceType; // 0: giá vốn, 1: giá lẻ, 2: giá sỉ
    private long createdAt;
    private long lastModifiedAt;

    public Order() {
        long now = System.currentTimeMillis();
        this.setOrderCode(String.format("%07d", now % 1046527));
        this.setCreatedAt(now);
        this.setStatus(Status.wait_confirm);
        this.setPrinted(false);
    }

    public boolean validateUser(String userId) {
        return this.getUserId().equals(userId);
    }

    public void validateRequest() {
        if (this.getCustomerPhone() == null) throw new GlobalException("customer name not null");
        if (this.getProducts() == null) throw new GlobalException("products not null");
    }
}
