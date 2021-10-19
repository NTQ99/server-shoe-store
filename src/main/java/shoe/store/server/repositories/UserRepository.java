package shoe.store.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    Boolean existsByUsername(String username);
}