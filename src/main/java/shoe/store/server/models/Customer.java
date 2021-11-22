package shoe.store.server.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import shoe.store.server.exceptions.GlobalException;

// customer model
@Document(collection = "customer")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class Customer {

    @Id
    private String id;

    private String customerCode;
    private String customerFirstName;
    private String customerLastName;
    private String customerName;
    private int customerGender; //0: khác 1: nam 2: nữ
    private String customerPhone;
    private String customerEmail;
    private String customerFacebook;
    private int defaultAddressId;
    private List<Address> customerAddresses = new ArrayList<>();
    private long createdAt;

    public Customer() {
        long now = System.currentTimeMillis();
        this.setCustomerCode(String.format("%07d", now % 1046527));
        this.setCreatedAt(now);
    };

    public Customer(String customerName, String customerPhone, String customerEmail) {
        long now = System.currentTimeMillis();
        this.setCustomerCode(String.format("%07d", now % 1046527));
        this.setCreatedAt(now);
        this.setCustomerPhone(customerPhone);
        this.setCustomerEmail(customerEmail);
        this.setCustomerName(customerName);
    };

    public Customer(String customerFirstName, String customerLastName, String customerPhone, String customerEmail) {
        long now = System.currentTimeMillis();
        this.setCustomerCode(String.format("%07d", now % 1046527));
        this.setCreatedAt(now);
        this.setCustomerFirstName(customerFirstName);
        this.setCustomerLastName(customerLastName);
        this.setCustomerPhone(customerPhone);
        this.setCustomerEmail(customerEmail);
    };

    public void addCustomerAddress(Address customerAddress) {
        if (!this.customerAddresses.contains(customerAddress)) {
            this.customerAddresses.add(customerAddress);
        }
    }

    public void updateCustomerAddress(int index, Address newCustomerAddress) {
        if (!this.customerAddresses.contains(newCustomerAddress)) {
            this.customerAddresses.set(index, newCustomerAddress);
        }
    }
    
    public void removeCustomerAddress(int index) {
        this.customerAddresses.remove(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        return this.getCustomerFirstName().equals(customer.getCustomerFirstName()) && this.getCustomerLastName().equals(customer.getCustomerFirstName()) && this.getCustomerPhone().equals(customer.getCustomerPhone());
    }

    public void validateRequest() {
        if (this.getCustomerPhone() == null) throw new GlobalException("customer phone not null");
        if (this.getCustomerFirstName() == null) throw new GlobalException("customer first name not null");
        if (this.getCustomerLastName() == null) throw new GlobalException("customer last name not null");
    }
}
