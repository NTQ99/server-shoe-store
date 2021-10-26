package shoe.store.server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import shoe.store.server.exceptions.GlobalException;
import lombok.*;

@Document(collection = "deliveryUnit")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryUnit {
    @Id
    private String id;

    private String userId;
    private String deliveryUnitName;
    private String appId = "OMS_UET";
    private String token;
    private String shopId;
    
    public DeliveryUnit(String userId, String deliveryUnitName, String token, String shopId) {
        this.userId = userId;
        this.deliveryUnitName = deliveryUnitName;
        this.token = token;
        this.shopId = shopId;
    }

    public boolean validateUser(String userId) {
        return this.getUserId().equals(userId);
    }

    public void validateRequest() {
        if (this.getDeliveryUnitName() == null) throw new GlobalException("delivery unit name not null");
        if (this.getToken() == null) throw new GlobalException("delivery token not null");
    }
}
