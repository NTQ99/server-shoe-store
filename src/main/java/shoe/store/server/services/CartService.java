package shoe.store.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shoe.store.server.models.Cart;
import shoe.store.server.repositories.CartRepository;

@Service("cartService")
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public Cart createCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public Cart getCartById(String id) {
        return cartRepository.findById(id).orElse(null);
    }

    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public Cart updateCart(Cart cartData, java.util.List<Cart.CartItem> items) {

        cartData.setItems(items);

        return cartRepository.save(cartData);
    }

    public Cart addProductToCart(Cart cartData, Cart.CartItem item) {

        cartData.addCartItem(item);

        return cartRepository.save(cartData);
    }

    public Cart updateProductToCartByIndex(Cart cartData, int index, int quantity) {

        cartData.updateCartItemByIndex(index, quantity);

        return cartRepository.save(cartData);
    }

    public Cart updateProductToCartByItem(Cart cartData, Cart.CartItem item, int quantity) {

        cartData.updateCartItemByItem(item, quantity);

        return cartRepository.save(cartData);
    }

    public void deleteCart(String id) {
        cartRepository.deleteById(id);
    }

    public void deleteCartByUserId(String userId) {
        cartRepository.deleteByUserId(userId);
    }

}
