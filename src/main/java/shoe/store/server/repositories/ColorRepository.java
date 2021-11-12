package shoe.store.server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.Color;

public interface ColorRepository extends MongoRepository<Color, String> {
    Color findByName(String name);
}
