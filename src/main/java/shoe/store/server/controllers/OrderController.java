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
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.security.jwt.JwtUtils;
import shoe.store.server.services.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService service;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/get")
    public ResponseEntity<BasePageResponse<List<Order>>> getAll(@RequestHeader("Authorization") String jwt) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        List<Order> orders = service.getAllOrders(userId);

        return new ResponseEntity<>(new BasePageResponse<>(orders, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/{id}")
    public ResponseEntity<BasePageResponse<Order>> getById(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Order order = service.getOrderById(id);

        if (!order.validateUser(userId)) throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);

        return new ResponseEntity<>(new BasePageResponse<>(order, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/customer/{id}")
    public ResponseEntity<BasePageResponse<List<Order>>> getByCustomerId(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        List<Order> orders = service.getOrderByCustomerId(id);

        for (Order order: orders) {
            if (!order.validateUser(userId)) throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        return new ResponseEntity<>(new BasePageResponse<>(orders, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<BasePageResponse<Order>> create(@RequestHeader("Authorization") String jwt, @RequestBody Order orderData) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));
        
        orderData.setUserId(userId);
        BasePageResponse<Order> response = new BasePageResponse<>(service.createOrder(orderData), ErrorMessage.StatusCode.CREATED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * 
     * @param userId id của tài khoản sở hữu
     * @param orderData Thông tin đơn hàng
     * @return Thông tin đơn hàng đã tạo
     */
    @PostMapping("/create/{idUser}")
    public ResponseEntity<BasePageResponse<Order>> openAPICreate(@PathVariable("idUser") String userId, @RequestBody Order orderData) {

        orderData.setUserId(userId);
        BasePageResponse<Order> response = new BasePageResponse<>(service.createOrder(orderData), ErrorMessage.StatusCode.CREATED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/update/{id}")
    public ResponseEntity<BasePageResponse<Order>> update(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id,
            @RequestBody Order newOrderData) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Order currOrderData = service.getOrderById(id);

        if (currOrderData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        if (!currOrderData.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        BasePageResponse<Order> response = new BasePageResponse<>(service.updateOrder(currOrderData, newOrderData), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<BasePageResponse<?>> delete(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Order currDeliveryData = service.getOrderById(id);

        if (currDeliveryData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        if (!currDeliveryData.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
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
    public ResponseEntity<BasePageResponse<Order>> sendOrder(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id,
            @RequestBody Delivery delivery) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Order order = service.getOrderById(id);

        if (order == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        if (!order.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        BasePageResponse<Order> response = new BasePageResponse<>(service.sendOrder(order, delivery), ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/update/status/{id}")
    public ResponseEntity<BasePageResponse<?>> updateStatus(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id,
            @RequestBody(required = false) Object request) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Order order = service.getOrderById(id);

        if (order == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        if (!order.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        if (request == null) {
            service.updateOrderStatus(order, null);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {});
            service.updateOrderStatus(order, map.get("status"));
        }
        BasePageResponse<?> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delivery/print/{id}")
    public ResponseEntity<BasePageResponse<String>> printOrder(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Order order = service.getOrderById(id);

        if (order == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        if (!order.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        BasePageResponse<String> response = new BasePageResponse<>(service.getPrintOrdersLink(Arrays.asList(order)),
                ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delivery/print")
    public ResponseEntity<BasePageResponse<String>> printAllOrder(@RequestHeader("Authorization") String jwt, @RequestBody(required = false) Object request) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {});
        String type = map.get("type");

        List<Order> orders = service.getAllOrders(userId);

        if (orders == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        orders = orders.stream()
                .filter(order -> order.getStatus().equals(Order.Status.await_trans))
                .collect(Collectors.toList());
                
        if (type == "new") {
            orders = orders.stream()
                .filter(order -> !order.isPrinted())
                .collect(Collectors.toList());
        }

        if (orders.isEmpty()) {
            throw new GlobalException("Tất cả các đơn hàng đã được in phiếu!");
        }

        BasePageResponse<String> response = new BasePageResponse<>(service.getPrintOrdersLink(orders),
                ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}