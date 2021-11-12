package shoe.store.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Brand;

public interface BrandRepository extends MongoRepository<Brand, String> {
    Brand findByName(String name);
}
