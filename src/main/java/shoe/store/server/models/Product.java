package shoe.store.server.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import shoe.store.server.exceptions.GlobalException;

@Document(collection = "product")
@Getter @Setter
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    private String userId;
    private String productCode;
    private String productName;
    private String productDetail;
    private List<String> productPhotos;
    private int[] price = new int[3]; // 0: giá vốn, 1: giá lẻ, 2: giá sỉ
    private double promotion;
    private int weight; // đơn vị gram
    private int stock;
    private long createdAt;

    public Product() {
        long now = System.currentTimeMillis();
        this.setProductCode(String.format("%07d", now % 1046527));
        this.setCreatedAt(now);
    };

    public boolean validateUser(String userId) {
        return this.getUserId().equals(userId);
    }
    
    public void validateRequest() {
        if (this.getProductName() == null) throw new GlobalException("product name not null");
        if (this.getPrice() == null) throw new GlobalException("price not null");
    }
}
