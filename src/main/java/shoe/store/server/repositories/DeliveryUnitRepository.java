package shoe.store.server.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import shoe.store.server.models.DeliveryUnit;

public interface DeliveryUnitRepository extends MongoRepository<DeliveryUnit, String> {
    List<DeliveryUnit> findByUserId(String id);
    void deleteByUserId(String userId);
    DeliveryUnit findByDeliveryUnitName(String deliveryUnitName);
}