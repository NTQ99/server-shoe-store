package shoe.store.server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document(collection = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    public enum ERole {
        ROLE_ADMIN, ROLE_SELLER, ROLE_BUYER
    }

    @Id
    private String id;

    private ERole name;

    public Role(ERole name) {
        this.name = name;
    }

}
