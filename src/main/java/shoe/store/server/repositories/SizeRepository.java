package shoe.store.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Size;

public interface SizeRepository extends MongoRepository<Size, String> {
    Size findByValue(int value);
}
