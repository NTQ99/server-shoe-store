package shoe.store.server.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(Role.ERole name);
}