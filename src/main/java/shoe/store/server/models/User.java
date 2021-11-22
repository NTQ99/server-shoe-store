package shoe.store.server.models;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;


@Document(collection = "user")
@Data
@AllArgsConstructor
public class User {

    public enum UserStatus {
        active,
        locked,
        banned
    }

    @Id
    private String id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private UserStatus status;

    @DBRef
    private Set<Role> roles = new HashSet<>();  

    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String customerCode;
    private long createdAt;

    public User() {
        long now = System.currentTimeMillis();
        this.setCreatedAt(now);
        this.setStatus(UserStatus.active);
    }

    public User(String username, String password) {
        long now = System.currentTimeMillis();
        this.setCreatedAt(now);
        this.setUsername(username);
        this.setPassword(password);
        this.setStatus(UserStatus.active);
    }
    
    public void updateData(User newData) {
        this.setEmail(newData.getEmail());
        this.setFirstName(newData.getFirstName());
        this.setLastName(newData.getLastName());
        this.setPhone(newData.getPhone());
        this.setCustomerCode(newData.getCustomerCode());
    }
}
