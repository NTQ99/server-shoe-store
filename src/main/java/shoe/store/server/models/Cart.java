package shoe.store.server.models;

import java.util.ArrayList;
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
    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> addCartItem(CartItem item) {
        this.items.add(item);
        return this.items;
    }
    public List<CartItem> updateCartItemByIndex(int index, int quantity) {
        if (quantity == 0) this.items.remove(index);
        else {
            CartItem item = this.items.get(index);
            item.setQuantity(quantity);
            this.items.set(index, item);
        }
        return this.items;
    }
    public List<CartItem> updateCartItemByItem(CartItem item, int quantity) {
        if (quantity == 0) this.items.remove(item);
        else {
            int index = this.items.indexOf(item);
            item.setQuantity(quantity);
            this.items.set(index, item);
        }
        return this.items;
    }

    public Cart(String userId) {
        this.userId = userId;
    }
}
