package shoe.store.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Address;
import shoe.store.server.models.Customer;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.services.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping("/get")
    public ResponseEntity<?> getAll() {

        List<Customer> customers = service.getAllCustomers();

        return new ResponseEntity<>(new BasePageResponse<>(customers, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {

        Customer customer = service.getCustomerById(id);

        return new ResponseEntity<>(new BasePageResponse<>(customer, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Customer customerData) {

        Customer currCustomerData = service.getCustomerByPhone(customerData.getCustomerPhone());

        if (currCustomerData != null) {
            throw new GlobalException(ErrorMessage.StatusCode.EXIST.message);
        }

        BasePageResponse<Customer> response = new BasePageResponse<>(service.createCustomer(customerData), ErrorMessage.StatusCode.CREATED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id,
            @RequestBody Customer newCustomerData) {

        Customer currCustomerData = service.getCustomerById(id);

        if (currCustomerData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        BasePageResponse<Customer> response = new BasePageResponse<>(service.updateCustomer(currCustomerData, newCustomerData), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/create/address/{id}")
    public ResponseEntity<?> addAddress(@PathVariable("id") String id,
            @RequestBody Address newAddress) {

        Customer currCustomerData = service.getCustomerById(id);

        if (currCustomerData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        BasePageResponse<Customer> response = new BasePageResponse<>(service.addCustomerAddress(currCustomerData, newAddress), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/update/address/{id}/{index}")
    public ResponseEntity<?> updateAddress(@PathVariable("id") String id, @PathVariable("index") String index,
            @RequestBody Address newAddressData) {

        Customer currCustomerData = service.getCustomerById(id);

        if (currCustomerData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        BasePageResponse<Customer> response = new BasePageResponse<>(service.updateCustomerAddress(currCustomerData, Integer.parseInt(index), newAddressData), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete/address/{id}/{index}")
    public ResponseEntity<?> removeAddress(@PathVariable("id") String id, @PathVariable("index") String index) {

        Customer currCustomerData = service.getCustomerById(id);

        if (currCustomerData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        BasePageResponse<Customer> response = new BasePageResponse<>(service.removeCustomerAddress(currCustomerData, Integer.parseInt(index)), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {

        Customer currDeliveryData = service.getCustomerById(id);

        if (currDeliveryData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        service.deleteCustomer(id);
        BasePageResponse<Customer> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAll() {

        service.deleteAllCustomers();
        BasePageResponse<Customer> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}