package shoe.store.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Product;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.security.jwt.JwtUtils;
import shoe.store.server.services.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService service;
    @Autowired
	private JwtUtils jwtUtils;

    @PostMapping("/get")
    public ResponseEntity<?> getAll() {

        List<Product> products = service.getAllProducts();

        return new ResponseEntity<>(new BasePageResponse<>(products, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/{id}")
    public ResponseEntity<?> getById(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Product product = service.getProductById(id);

        if (!product.validateUser(userId)) throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);

        return new ResponseEntity<>(new BasePageResponse<>(product, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String jwt, @RequestBody Product productData) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));
        
        Product currProductData = service.getProductByName(userId, productData.getProductName());

        if (currProductData != null) {
            throw new GlobalException(ErrorMessage.StatusCode.EXIST.message);
        }

        productData.setUserId(userId);
        BasePageResponse<Product> response = new BasePageResponse<>(service.createProduct(productData), ErrorMessage.StatusCode.CREATED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id,
            @RequestBody Product newProductData) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Product currProductData = service.getProductById(id);

        if (currProductData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        if (!currProductData.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        BasePageResponse<Product> response = new BasePageResponse<>(service.updateProduct(currProductData, newProductData), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        Product currDeliveryData = service.getProductById(id);

        if (currDeliveryData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        if (!currDeliveryData.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        service.deleteProduct(id);
        BasePageResponse<Product> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAll(@RequestHeader("Authorization") String jwt) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        service.deleteAllProducts(userId);
        BasePageResponse<Product> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}