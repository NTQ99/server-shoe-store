package shoe.store.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Category findByCategoryName(String name);
}
