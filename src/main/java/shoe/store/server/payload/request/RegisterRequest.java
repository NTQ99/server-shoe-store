package shoe.store.server.payload.request;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoe.store.server.models.Address;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
 
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String roleKey;
    
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String birthday;
    private Address address;
}
