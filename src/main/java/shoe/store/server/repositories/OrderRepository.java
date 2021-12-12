package shoe.store.server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
    Order findByOrderCode(String code);
    List<Order> findByUserId(String userId);
    List<Order> findByCustomerId(String customerId);
    Page<Order> findByCustomerNameContainingIgnoreCase(String name, Pageable paging);
    Page<Order> findByCustomerPhoneContaining(String phone, Pageable paging);
    Page<Order> findByStatus(String status, Pageable paging);
    Page<Order> findByCustomerNameContainingIgnoreCaseAndStatus(String name, String status, Pageable paging);
    Page<Order> findByCustomerPhoneContainingAndStatus(String phone, String status, Pageable paging);
    Page<Order> findByCreatedAtBetween(String createdAtFrom, String createdAtTo, Pageable paging);
    int countByStatus(Order.Status status);
    void deleteByUserId(String userId);
}
