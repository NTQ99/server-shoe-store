package shoe.store.server.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String ward="";
    private String district="";
    private String province="";
    // address detail
    private String detail="";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        return this.ward.equals(address.ward) && this.district.equals(address.district)
                && this.province.equals(address.province) && this.detail.equals(address.detail);
    }
}