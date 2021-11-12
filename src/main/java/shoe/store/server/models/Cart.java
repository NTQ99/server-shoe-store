package shoe.store.server.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Data
    public static class CartItem {
        private String productId;
        private int quantity;
    }
    @Id
    private String id;

    private long orderDate;
    private String orderStatus;
    private String shippingDate;
    private String userId;
    private List<CartItem> items;
}
