package shoe.store.server.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shoe.store.server.models.DeliveryUnit;
import shoe.store.server.repositories.DeliveryUnitRepository;

@Service("deliveryUnitService")
public class DeliveryUnitService {
    @Autowired
    private DeliveryUnitRepository deliveryUnitRepository;

    public DeliveryUnit createDeliveryUnit(DeliveryUnit deliveryUnit) {
        return deliveryUnitRepository.save(deliveryUnit);
    }

    public DeliveryUnit getDeliveryUnitById(String id) {
        return deliveryUnitRepository.findById(id).orElse(null);
    }

    public DeliveryUnit getDeliveryUnitByName(String userId, String name) {
        List<DeliveryUnit> deliveryUnits = deliveryUnitRepository.findByUserId(userId);
        deliveryUnits = deliveryUnits.stream().filter(deliveryUnit -> deliveryUnit.getDeliveryUnitName().equals(name))
                .collect(Collectors.toList());
        if (deliveryUnits.isEmpty()) {
            return null;
        }
        return deliveryUnits.get(0);
    }

    public List<DeliveryUnit> getAllDeliveryUnits(String userId) {
        return deliveryUnitRepository.findByUserId(userId);
    }

    public DeliveryUnit updateDeliveryUnit(String id, DeliveryUnit newDeliveryData) {
        DeliveryUnit deliveryUnitData = deliveryUnitRepository.findById(id).orElse(null);

        deliveryUnitData.setDeliveryUnitName(newDeliveryData.getDeliveryUnitName());
        deliveryUnitData.setShopId(newDeliveryData.getShopId());
        deliveryUnitData.setToken(newDeliveryData.getToken());

        return deliveryUnitRepository.save(deliveryUnitData);
    }

    public void deleteDeliveryUnit(String id) {
        deliveryUnitRepository.deleteById(id);
    }

    public void deleteAllDeliveryUnits(String userId) {
        deliveryUnitRepository.deleteByUserId(userId);
    }

}
