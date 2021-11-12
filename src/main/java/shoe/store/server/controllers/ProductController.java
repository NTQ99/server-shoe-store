package shoe.store.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Product;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.services.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService service;

    @PostMapping("/get")
    public ResponseEntity<?> getAll() {
        
        List<Product> products = service.getAllProducts();

        return new ResponseEntity<>(new BasePageResponse<>(products, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {

        Product product = service.getProductById(id);
        if (product == null) product = service.getProductByCode(id);
        
        return new ResponseEntity<>(new BasePageResponse<>(product, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);
        
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<?> create(@RequestBody Product productData) {
        
        BasePageResponse<Product> response = new BasePageResponse<>(service.createProduct(productData), ErrorMessage.StatusCode.CREATED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }
    
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<?> update(@PathVariable("id") String id,
            @RequestBody Product newProductData) {

        Product currProductData = service.getProductById(id);

        if (currProductData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        BasePageResponse<Product> response = new BasePageResponse<>(service.updateProduct(currProductData, newProductData), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {

        Product currDeliveryData = service.getProductById(id);

        if (currDeliveryData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

        service.deleteProduct(id);
        BasePageResponse<Product> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<?> deleteAll() {

        service.deleteAllProducts();
        BasePageResponse<Product> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}