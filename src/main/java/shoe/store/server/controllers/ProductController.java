package shoe.store.server.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Color;
import shoe.store.server.models.Product;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.payload.request.QueryRequest;
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

    @PostMapping("/get/group")
    public ResponseEntity<?> getGroupByProductCode(@RequestBody(required = false) QueryRequest queryRequest) {
        
        return new ResponseEntity<>(new BasePageResponse<>(service.groupByProductCode(queryRequest), ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {

        Product product = service.getProductById(id);
        if (product == null) {
            return new ResponseEntity<>(new BasePageResponse<>(service.getProductsByCode(id), ErrorMessage.StatusCode.OK.message), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new BasePageResponse<>(product, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<?> create(@RequestBody Product productData) {
        
        productData.validateRequest();
        
        BasePageResponse<Product> response = new BasePageResponse<>(service.saveProduct(productData), ErrorMessage.StatusCode.CREATED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }
    
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<?> update(@PathVariable("id") String id,
    @RequestBody Product newProductData) {
        
        newProductData.validateRequest();

        BasePageResponse<Product> response = new BasePageResponse<>(service.updateProduct(id, newProductData), ErrorMessage.StatusCode.MODIFIED.message);
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

    @PostMapping("/color/{id}")
    public String getColorById(@PathVariable("id") String id) {
        return service.getColorCode(id);
    }
    @PostMapping("/color")
    public List<Color> getAllColors() {
        return service.getAllColors();
    }

    @PostMapping("/category/count")
    public ResponseEntity<?> getNumOfProductByCategory() {
        BasePageResponse<Map<?,?>> response = new BasePageResponse<>(service.getNumOfProductByCategory(), ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/brand/count")
    public ResponseEntity<?> getNumOfProductByBrand() {
        BasePageResponse<Map<?,?>> response = new BasePageResponse<>(service.getNumOfProductByBrand(), ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/color/count")
    public ResponseEntity<?> getNumOfProductByColor() {
        BasePageResponse<Map<?,?>> response = new BasePageResponse<>(service.getNumOfProductByColor(), ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}