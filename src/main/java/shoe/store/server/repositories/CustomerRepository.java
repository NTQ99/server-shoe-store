package shoe.store.server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Customer findByCustomerCode(String customerCode);
    List<Customer> findByUserId(String userId);
    Page<Customer> findByCustomerNameContainingIgnoreCase(String customerName, Pageable paging);
    Page<Customer> findByCustomerPhoneContaining(String customerPhone, Pageable paging);
    Customer findOneByCustomerPhone(String customerPhone);
    void deleteByUserId(String userId);
}
