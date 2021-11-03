package shoe.store.server.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Customer;
import shoe.store.server.models.Delivery;
import shoe.store.server.models.DeliveryUnit;
import shoe.store.server.models.Order;
import shoe.store.server.models.Product;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.repositories.OrderRepository;

@Service("orderService")
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private DeliveryUnitService deliveryUnitService;
    @Autowired
    private DeliveryService deliveryService;

    public Order createOrder(Order orderData) {

        if (orderData.getProducts() == null) throw new GlobalException("products not null");

        int totalPrice = 0;
        for (Order.Item item : orderData.getProducts()) {
            Product product = productService.getProductByName(item.getProductName());
            if (product == null) {
                throw new GlobalException("not found product with name: " + item.getProductName());
            } else item.setProductId(product.getId());
            totalPrice += product.getPrice()[orderData.getPriceType()] *item.getQuantity();
        }

        Customer customerData = customerService.getCustomerByPhone(orderData.getUserId(), orderData.getCustomerPhone());
        if (customerData == null) {
            Customer customer = new Customer(orderData.getUserId(), orderData.getCustomerName(), orderData.getCustomerPhone());
            customer.setCustomerAddresses(Arrays.asList(orderData.getDeliveryTo()));
            orderData.setCustomerId(customerService.createCustomer(customer).getId());
        } else {
            orderData.setCustomerId(customerData.getId());
            customerService.addCustomerAddress(customerData, orderData.getDeliveryTo());
        }

        if (orderData.getTotalPrice() == 0) orderData.setTotalPrice(totalPrice);
        if (orderData.getCodAmount() == -1) orderData.setCodAmount(totalPrice);

        return orderRepository.save(orderData);
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getOrderByCustomerId(String id) {
        return orderRepository.findByCustomerId(id);
    }

    public Order getOrderByCode(String code) {
        return orderRepository.findByOrderCode(code);
    }

    public Page<Order> getOrderByCreatedDateBetween(String createdDateFrom, String createdDateTo, Pageable paging) {
        return orderRepository.findByCreatedAtBetween(createdDateFrom, createdDateTo, paging);
    }

    public Page<Order> getOrderByStatus(String status, Pageable paging) {
        return orderRepository.findByStatus(status, paging);
    }

    public Page<Order> findOrderByCustomerName(String name, Pageable paging) {
        return orderRepository.findByCustomerNameContainingIgnoreCase(name, paging);
    }

    public Page<Order> findOrderByCustomerPhone(String phone, Pageable paging) {
        return orderRepository.findByCustomerPhoneContaining(phone, paging);
    }

    public Page<Order> findOrderByCustomerNameAndStatus(String name, String status, Pageable paging) {
        return orderRepository.findByCustomerNameContainingIgnoreCaseAndStatus(name, status, paging);
    }

    public Page<Order> findOrderByCustomerPhoneAndStatus(String phone, String status, Pageable paging) {
        return orderRepository.findByCustomerPhoneContainingAndStatus(phone, status, paging);
    }

    public List<Order> getAllOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Page<Order> getAllOrdersCondition(String generalSearch, String status, Pageable paging) {
        if (generalSearch == "") {
            return this.getOrderByStatus(status, paging);
        } else {
            try {
                Long.parseLong(generalSearch);
                if (status == "") {
                    return this.findOrderByCustomerPhone(generalSearch, paging);
                } else {
                    return this.findOrderByCustomerPhoneAndStatus(generalSearch, status, paging);
                }
            } catch (NumberFormatException nfe) {
                if (status == "") {
                    return this.findOrderByCustomerName(generalSearch, paging);
                } else {
                    return this.findOrderByCustomerNameAndStatus(generalSearch, status, paging);
                }
            }

        }
    }

    public Order updateOrder(Order orderData, Order newOrderData) {

        if (!orderData.getCustomerPhone().equals(newOrderData.getCustomerPhone())) {
            Customer newCustomerData = customerService.getCustomerByPhone(orderData.getUserId(), newOrderData.getCustomerPhone());
            if (newCustomerData == null) {
                newCustomerData = new Customer(orderData.getUserId(), newOrderData.getCustomerName(), newOrderData.getCustomerPhone());
                newCustomerData.setCustomerAddresses(Arrays.asList(orderData.getDeliveryTo()));
                orderData.setCustomerId(customerService.createCustomer(newCustomerData).getId());
            } else {
                customerService.addCustomerAddress(newCustomerData, orderData.getDeliveryTo());
                orderData.setCustomerId(newCustomerData.getId());
            }
            orderData.setCustomerName(newOrderData.getCustomerName());
            orderData.setCustomerPhone(newOrderData.getCustomerPhone());
        }

        if (!orderData.getDeliveryTo().equals(newOrderData.getDeliveryTo())) {
            Customer customerData = customerService.getCustomerByCode(orderData.getCustomerId());
            customerService.addCustomerAddress(customerData, newOrderData.getDeliveryTo());
            orderData.setDeliveryTo(newOrderData.getDeliveryTo());
        }

        for (Order.Item item : newOrderData.getProducts()) {
            if (item.getProductId() == null) {
                Product product = productService.getProductByName(item.getProductName());
                if (product == null) {
                    throw new GlobalException("not found product with name: " + item.getProductName());
                } else item.setProductId(product.getId());
            }
        }

        orderData.setProducts(newOrderData.getProducts());
        orderData.setDeliveryUnitName(newOrderData.getDeliveryUnitName());
        orderData.setCodAmount(newOrderData.getCodAmount());
        orderData.setPriceType(newOrderData.getPriceType());
        orderData.setTotalPrice(newOrderData.getTotalPrice());
        orderData.setShipFee(newOrderData.getShipFee());
        orderData.setNote(newOrderData.getNote());

        return orderRepository.save(orderData);
    }

    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    public void deleteAllOrders(String userId) {
        orderRepository.deleteByUserId(userId);
    }

    public Order sendOrder(Order order, Delivery delivery) {

        List<Product> products = new ArrayList<>();
        if (order.getProducts() == null)
            throw new GlobalException("products not null");
        for (Order.Item item : order.getProducts()) {
            Product product = productService.getProductById(item.getProductId());
            if (product == null)
                throw new GlobalException("not found product with id: " + item.getProductId());
            if (product.getStock() < item.getQuantity())
                throw new GlobalException(ErrorMessage.StatusCode.OUT_OF_STOCK.message);

            product.setStock(product.getStock() - item.getQuantity());
            productService.createProduct(product);

            products.add(product);
        }

        DeliveryUnit deliveryUnit = deliveryUnitService.getDeliveryUnitByName(order.getUserId(), delivery.getDeliveryUnitName());
        if (deliveryUnit == null)
            throw new GlobalException("delivery unit not found with name: " + order.getDeliveryUnitName());

        if (deliveryUnit.getDeliveryUnitName().equals("GHN")) {
            delivery.setRequest(products, order);
            delivery.setTo_district_id(deliveryService.GHNGetDistrictId(deliveryUnit.getToken(),
                    order.getDeliveryTo().getProvince(), order.getDeliveryTo().getDistrict()));
            delivery.setTo_ward_code(deliveryService.GHNGetWardCode(deliveryUnit.getToken(),
                    delivery.getTo_district_id(), order.getDeliveryTo().getWard()));
            order.setDeliveryCode(
                    deliveryService.GHNCreateOrder(deliveryUnit.getToken(), deliveryUnit.getShopId(), delivery));
            order.setStatus(Order.Status.await_trans);
            order.setDeliveryUnitName(delivery.getDeliveryUnitName());
            order.setShipFee(delivery.getShipFee());
            return orderRepository.save(order);
        } else
            throw new GlobalException("delivery unit unavailable");
    }

    public void updateOrderStatus(Order order, String status) {

        if (status != null) {
            order.setStatus(Order.Status.valueOf(status));
            orderRepository.save(order);
            return;
        }

        DeliveryUnit deliveryUnit = deliveryUnitService.getDeliveryUnitByName(order.getUserId(), order.getDeliveryUnitName());
        if (deliveryUnit == null)
            throw new GlobalException("delivery unit not found with name: " + order.getDeliveryUnitName());
        
        if (deliveryUnit.getDeliveryUnitName().equals("GHN")) {
            String newStatus = deliveryService.GHNGetStatus(deliveryUnit.getToken(), deliveryUnit.getShopId(), order.getDeliveryCode());
            switch (newStatus) {
                case "cancel":
                    order.setStatus(Order.Status.canceled);
                    break;
                case "returned":
                    order.setStatus(Order.Status.fail);
                    break;
                default:
                    order.setStatus(Order.Status.await_trans);
                    break;
            }
            orderRepository.save(order);
        } else
            throw new GlobalException("delivery unit unavailable");
    }

    public String getPrintOrdersLink(List<Order> orders) {
        List<String> orderCodes = new ArrayList<>();
        for (Order order: orders) {
            orderCodes.add(order.getDeliveryCode());
            order.setPrinted(true);
            orderRepository.save(order);
        }
        DeliveryUnit deliveryUnit = deliveryUnitService.getDeliveryUnitByName(orders.get(0).getUserId(), orders.get(0).getDeliveryUnitName());
        if (deliveryUnit == null)
            throw new GlobalException("delivery unit not found with name: " + orders.get(0).getDeliveryUnitName());
        
        if (deliveryUnit.getDeliveryUnitName().equals("GHN")) {
            return deliveryService.GHNGetPrintOrdersLink(deliveryUnit.getToken(), orderCodes);
        } else
        throw new GlobalException("delivery unit unavailable");
    }

}
