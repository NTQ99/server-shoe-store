package shoe.store.server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import shoe.store.server.models.Address;
import shoe.store.server.models.Customer;
import shoe.store.server.repositories.CustomerRepository;

@Service("customerService")
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        if (customer.getCustomerName() == null || customer.getCustomerName() == "") {
            customer.setCustomerName(customer.getCustomerFirstName() + " " + customer.getCustomerLastName());
        }
        if ((customer.getCustomerFirstName() == null || customer.getCustomerFirstName() == "")
                && (customer.getCustomerLastName() == null || customer.getCustomerLastName() == "")) {
            String[] names = customer.getCustomerName().split(" ", 2);
            String firstName = "";
            String lastName = "";
            if (names.length >= 1) firstName = names[0].trim();
            if (names.length >= 2) lastName = names[1].trim();
            customer.setCustomerName((firstName + " " + lastName).trim());
        }
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(String id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null) {
            return customer;
        } else {
            return this.getCustomerByCode(id);
        }
    }

    public Customer getCustomerByCode(String code) {
        return customerRepository.findByCustomerCode(code);
    }

    public Customer getCustomerByPhone(String phone) {
        return customerRepository.findOneByCustomerPhone(phone);
    }

    public Page<Customer> findCustomerByPhone(String phone, Pageable paging) {
        return customerRepository.findByCustomerPhoneContaining(phone, paging);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Customer customerData, Customer newCustomerData) {

        customerData.setCustomerFirstName(newCustomerData.getCustomerFirstName());
        customerData.setCustomerLastName(newCustomerData.getCustomerLastName());
        customerData.setCustomerName(newCustomerData.getCustomerName());
        customerData.setCustomerGender(newCustomerData.getCustomerGender());
        customerData.setCustomerPhone(newCustomerData.getCustomerPhone());
        customerData.setCustomerEmail(newCustomerData.getCustomerEmail());
        customerData.setCustomerFacebook(newCustomerData.getCustomerFacebook());
        customerData.setCustomerAddresses(newCustomerData.getCustomerAddresses());
        customerData.setDefaultAddressId(newCustomerData.getDefaultAddressId());

        return customerRepository.save(customerData);
    }

    public Customer addCustomerAddress(Customer customerData, Address newAddress) {

        customerData.addCustomerAddress(newAddress);

        return customerRepository.save(customerData);
    }

    public Customer updateCustomerAddress(Customer customerData, int index, Address newAddressData) {

        customerData.updateCustomerAddress(index, newAddressData);

        return customerRepository.save(customerData);
    }

    public Customer removeCustomerAddress(Customer customerData, int index) {

        customerData.removeCustomerAddress(index);

        return customerRepository.save(customerData);
    }

    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }

    public void deleteAllCustomers() {
        customerRepository.deleteAll();
    }

}
