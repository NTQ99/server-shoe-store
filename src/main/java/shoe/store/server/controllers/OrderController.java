package shoe.store.server.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Delivery;
import shoe.store.server.models.Order;
import shoe.store.server.models.Product;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.security.jwt.JwtUtils;
import shoe.store.server.services.OrderService;
import shoe.store.server.services.ProductService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService service;

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/get")
    public ResponseEntity<BasePageResponse<List<Order>>> getAll(@RequestHeader("Authorization") String jwt) {

        List<Order> orders = service.getAllOrders();

        return new ResponseEntity<>(new BasePageResponse<>(orders, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/{id}")
    public ResponseEntity<BasePageResponse<Order>> getById(@PathVariable("id") String id, @RequestBody(required = false) String phone) {

        Order order = service.getOrderById(id);

        if (order == null) {
            order = service.getOrderByCode(id);
        }

        if (order == null || phone == null || (order != null && phone != null && !order.getCustomerPhone().equals(phone))) {
            return new ResponseEntity<>(new BasePageResponse<>(null, ErrorMessage.StatusCode.NOT_FOUND.message), HttpStatus.OK);
        }

        for (Order.Item item : order.getProducts()) {
            Product product = productService.getProductById(item.getProductId());
            item.setProductId(Double.toString(product.getPrice()));
        }

        return new ResponseEntity<>(new BasePageResponse<>(order, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/customer/{id}")
    public ResponseEntity<BasePageResponse<List<Order>>> getByCustomerId(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        List<Order> orders = service.getOrderByCustomerId(id);

        return new ResponseEntity<>(new BasePageResponse<>(orders, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get-report")
    public ResponseEntity<BasePageResponse<?>> getReport() {

        return new ResponseEntity<>(new BasePageResponse<>(service.getReport(), ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<BasePageResponse<Order>> create(@RequestHeader("Authorization") String jwt, @RequestBody Order orderData) {

        if (!jwt.equals("undefined")) {
            String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));
            orderData.setUserId(userId);
        }
        orderData.validateRequest();
        BasePageResponse<Order> response = new BasePageResponse<>(service.createOrder(orderData), ErrorMessage.StatusCode.CREATED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/update/{id}")
    public ResponseEntity<BasePageResponse<Order>> update(@PathVariable("id") String id,
            @RequestBody Order newOrderData) {

        Order currOrderData = service.getOrderById(id);

        if (currOrderData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        BasePageResponse<Order> response = new BasePageResponse<>(service.updateOrderAddress(currOrderData, newOrderData), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<BasePageResponse<?>> delete(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        Order currDeliveryData = service.getOrderById(id);

        if (currDeliveryData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        service.deleteOrder(id);
        BasePageResponse<?> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete")
    public ResponseEntity<BasePageResponse<?>> deleteAll(@RequestHeader("Authorization") String jwt) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        service.deleteAllOrders(userId);
        BasePageResponse<?> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delivery/{id}")
    public ResponseEntity<BasePageResponse<Order>> sendOrder(@PathVariable("id") String id,
            @RequestBody Delivery delivery) {

        BasePageResponse<Order> response = new BasePageResponse<>(service.sendOrder(id, delivery), ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/update/status/{id}")
    public ResponseEntity<BasePageResponse<?>> updateStatus(@PathVariable("id") String id,
            @RequestBody(required = false) String status) {

        Order order = service.getOrderById(id);

        if (order == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        service.updateOrderStatus(order, status);
        BasePageResponse<?> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}