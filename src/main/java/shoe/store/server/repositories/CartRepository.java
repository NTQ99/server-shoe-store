package shoe.store.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Cart;

public interface CartRepository extends MongoRepository<Cart, String> {
    Cart findByUserId(String userId);
    void deleteByUserId(String userId);
}
