package shoe.store.server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByPhone(String phone);
    Page<User> findByUsernameContaining(String username, Pageable paging);
}