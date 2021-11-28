package shoe.store.server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Product;
import shoe.store.server.models.ProductGroup;

public interface ProductRepository extends MongoRepository<Product, String> {
    Product findByProductCode(String code);
    List<Product> findByProductCodeContaining(String code);
    Product findOneByProductName(String name);
    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable paging);

    @Aggregation(pipeline = {"{ '$group': { '_id' : '$productCode', 'products': { '$push': '$$ROOT' } } }", "{ '$sort' : { 'products.createdAt' : 1 } }" })
    List<ProductGroup> findProductsGroupByProductCode();
}
