package shoe.store.server.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import shoe.store.server.models.Product;
import shoe.store.server.repositories.ProductRepository;

@Service("productService")
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product getProductByCode(String code) {
        return productRepository.findByProductCode(code);
    }

    public Product getProductByName(String userId, String name) {
        List<Product> products = productRepository.findByUserId(userId);
        products = products.stream().filter(product -> product.getProductName().equals(name))
                .collect(Collectors.toList());
        if (products.isEmpty()) {
            return null;
        }

        return products.get(0);
    }

    public Page<Product> findProductByName(String name, Pageable paging) {
        return productRepository.findByProductNameContainingIgnoreCase(name, paging);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Product productData, Product newProductData) {

        productData.setProductName(newProductData.getProductName());
        productData.setProductDetail(newProductData.getProductDetail());
        productData.setProductPhotos(newProductData.getProductPhotos());
        productData.setPromotion(newProductData.getPromotion());
        productData.setPrice(newProductData.getPrice());
        productData.setWeight(newProductData.getWeight());
        productData.setStock(newProductData.getStock());

        return productRepository.save(productData);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public void deleteAllProducts(String userId) {
        productRepository.deleteByUserId(userId);
    }

}
