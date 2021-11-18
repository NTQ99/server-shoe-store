package shoe.store.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import shoe.store.server.models.Cart;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.security.jwt.JwtUtils;
import shoe.store.server.services.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService service;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/get")
    public ResponseEntity<?> get(@RequestHeader("Authorization") String jwt) {
        
        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Cart cart = service.getCartByUserId(userId);

        return new ResponseEntity<>(new BasePageResponse<>(cart, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {

        Cart cart = service.getCartById(id);
        
        return new ResponseEntity<>(new BasePageResponse<>(cart, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);
        
    }

    @PostMapping(value={"/update", "/update/{index}"})
    public ResponseEntity<?> updateItemCart(@RequestHeader("Authorization") String jwt, @RequestBody Cart.CartItem item, @PathVariable(required = false, name = "index") String index) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Cart cartData = service.getCartByUserId(userId);
        Cart newCart = null;
        if (cartData == null) {
            cartData = new Cart(userId);
            cartData.addCartItem(item);
            newCart = service.createCart(cartData);
        } else {
            if (index == null) newCart = service.addProductToCart(cartData, item);
            else {
                int i = Integer.parseInt(index);
                newCart = service.updateProductToCartByIndex(cartData, i, item.getQuantity());
            }
        }
        
        BasePageResponse<Cart> response = new BasePageResponse<>(newCart, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }

    @PostMapping("/updateall")
    public ResponseEntity<?> updateCart(@RequestHeader("Authorization") String jwt, @RequestBody List<Cart.CartItem> items) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Cart cartData = service.getCartByUserId(userId);
        if (cartData == null) cartData = new Cart(userId);
        BasePageResponse<Cart> response = new BasePageResponse<>(service.updateCart(cartData, items), ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String jwt) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        service.deleteCartByUserId(userId);
        BasePageResponse<Cart> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}