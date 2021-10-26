package shoe.store.server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    Product findByProductCode(String code);
    Product findOneByProductName(String name);
    List<Product> findByUserId(String userId);
    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable paging);
    void deleteByUserId(String userId);
}
