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
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private DeliveryUnitService deliveryUnitService;
    @Autowired
    private DeliveryService deliveryService;

    public Order createOrder(Order orderData) {

        int totalPrice = 0;
        for (Order.Item item : orderData.getProducts()) {
            Product product = productService.getProductById(item.getProductId());
            if (product == null) {
                throw new GlobalException("not found product with name: " + item.getProductName());
            }
            totalPrice += product.getPrice()*item.getQuantity();
            if (product.getStock() < item.getQuantity())
                throw new GlobalException(ErrorMessage.StatusCode.OUT_OF_STOCK.message);
            product.setStock(product.getStock() - item.getQuantity());
            productService.saveProduct(product);
        }

        Customer customerData = customerService.getCustomerByPhone(orderData.getCustomerPhone());
        if (customerData == null) {
            Customer customer = new Customer(orderData.getCustomerName(), orderData.getCustomerPhone(), orderData.getCustomerEmail());
            customer.setCustomerAddresses(Arrays.asList(orderData.getDeliveryTo()));
            orderData.setCustomerId(customerService.createCustomer(customer).getId());
        } else {
            orderData.setCustomerId(customerData.getId());
            orderData.setCustomerName(customerData.getCustomerName());
            orderData.setCustomerEmail(customerData.getCustomerEmail());
            customerService.addCustomerAddress(customerData, orderData.getDeliveryTo());
        }

        if (orderData.getTotalPrice() == 0) orderData.setTotalPrice(totalPrice);
        if (orderData.getCodAmount() == -1) orderData.setCodAmount(totalPrice);

        Order newOrder = orderRepository.save(orderData);
        if (newOrder != null) {
            cartService.deleteCartByUserId(newOrder.getUserId());
        }
        return newOrder;
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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
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
            Customer newCustomerData = customerService.getCustomerByPhone(newOrderData.getCustomerPhone());
            if (newCustomerData == null) {
                newCustomerData = new Customer(newOrderData.getCustomerName().split("#")[0], newOrderData.getCustomerName().split("#")[1], newOrderData.getCustomerPhone(), newOrderData.getCustomerEmail());
                newCustomerData.setCustomerAddresses(Arrays.asList(orderData.getDeliveryTo()));
                orderData.setCustomerId(customerService.createCustomer(newCustomerData).getId());
            } else {
                customerService.addCustomerAddress(newCustomerData, orderData.getDeliveryTo());
                orderData.setCustomerId(newCustomerData.getId());
            }
            orderData.setCustomerName(newOrderData.getCustomerName());
            orderData.setCustomerPhone(newOrderData.getCustomerPhone());
            orderData.setCustomerEmail(newOrderData.getCustomerEmail());
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

    public Order updateOrderAddress(Order orderData, Order newOrderData) {

        if (!orderData.getDeliveryTo().equals(newOrderData.getDeliveryTo())) {
            Customer customerData = customerService.getCustomerById(orderData.getCustomerId());
            customerService.addCustomerAddress(customerData, newOrderData.getDeliveryTo());
            orderData.setDeliveryTo(newOrderData.getDeliveryTo());
        }

        return orderRepository.save(orderData);
    }

    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    public void deleteAllOrders(String userId) {
        orderRepository.deleteByUserId(userId);
    }

    public Order sendOrder(String id, Delivery delivery) {

        Order order = this.getOrderById(id);

        if (order == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        order.setStatus(Order.Status.await_trans);
        order.setDeliveryUnitName(delivery.getDeliveryUnitName());
        order.setShipFee(delivery.getShipFee());
        return orderRepository.save(order);
    }

    public void updateOrderStatus(Order order, String status) {

        if (status != null) {
            if (status.equals("canceled") || status.equals("fail")) {
                for (Order.Item item : order.getProducts()) {
                    Product product = productService.getProductById(item.getProductId());
                    product.setStock(product.getStock() + item.getQuantity());
                    productService.saveProduct(product);
                }
            }
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
