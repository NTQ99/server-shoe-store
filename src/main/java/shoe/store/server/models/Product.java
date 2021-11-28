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
        if (this.getProductName() == null) throw new GlobalException("Tên sản phẩm không đươc bỏ trống!");
        if (this.getPrice() == 0) throw new GlobalException("Giá sản phẩm không đươc bỏ trống!");
        if (this.getProductPhotos() == null) throw new GlobalException("Hình ảnh sản phẩm không đươc bỏ trống!");

        String colorName = this.getColor().toUpperCase();
        if (colorName.length() != 2) {
            String colorCode = "XX";
            if (colorName.contains("ĐEN")) colorCode = "DE";
            else if (colorName.contains("XANH")) {
                if (colorName.contains("LỤC")) colorCode = "XL";
                else colorCode = "XB";
            }
            else if (colorName.contains("BE")) colorCode = "BE";
            else if (colorName.contains("NÂU")) colorCode = "NA";
            else if (colorName.contains("ĐỎ")) colorCode = "DO";
            else if (colorName.contains("TÍM")) colorCode = "TI";
            else if (colorName.contains("VÀNG")) colorCode = "VA";
            else if (colorName.contains("XÁM")) colorCode = "XA";
            else if (colorName.contains("CAM")) colorCode = "CA";
            else if (colorName.contains("HỒNG")) colorCode = "HO";
            else if (colorName.contains("TRẮNG")) colorCode = "TR";
    
            this.setColor(colorCode);
        }
    }
}
