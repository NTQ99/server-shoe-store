package shoe.store.server.models;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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

    private boolean productStatus;
    private String productCode;
    private String productName;
    private String productDetail;
    private String shortTitle;
    private String materials;
    private List<String> productPhotos;
    @Min(16) @Max(47)
    private int size;
    private String color;
    private String brand;
    private String category;
    private double price;
    private double promotion;
    private int weight; // đơn vị gram
    private int stock;
    private long createdAt;

    public Product() {
        long now = System.currentTimeMillis();
        this.setProductCode(String.format("%07d", now % 1046527));
        this.setCreatedAt(now);
    };
    
    public void validateRequest() {
        if (this.getProductName() == null) throw new GlobalException("product name not null");
        if (this.getPrice() == 0) throw new GlobalException("price not null");
    }
}
