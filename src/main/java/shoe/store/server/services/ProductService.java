package shoe.store.server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import shoe.store.server.models.Product;
import shoe.store.server.models.Size;
import shoe.store.server.models.Color;
import shoe.store.server.models.Brand;
import shoe.store.server.repositories.BrandRepository;
import shoe.store.server.repositories.ColorRepository;
import shoe.store.server.repositories.ProductRepository;
import shoe.store.server.repositories.SizeRepository;

@Service("productService")
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private BrandRepository brandRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product getProductByCode(String code) {
        return productRepository.findByProductCode(code);
    }

    public Product getProductByName(String name) {
        return productRepository.findOneByProductName(name);
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
        productData.setCategory(newProductData.getCategory());
        productData.setWeight(newProductData.getWeight());
        productData.setStock(newProductData.getStock());
        productData.setSize(newProductData.getSize());
        productData.setBrand(newProductData.getBrand());
        productData.setColor(newProductData.getColor());
        productData.setShortTitle(newProductData.getShortTitle());

        return productRepository.save(productData);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    public Size getSizeByValue(int value) {
        return sizeRepository.findByValue(value);
    }

    public Size createSize(Size size) {
        return sizeRepository.save(size);
    }

    public void deleteSize(Size size) {
        sizeRepository.delete(size);
    }

    public void deleteAllSizes() {
        sizeRepository.deleteAll();
    }

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public Color getColorByValue(String name) {
        return colorRepository.findByName(name);
    }

    public Color createColor(Color color) {
        return colorRepository.save(color);
    }

    public void deleteColor(Color color) {
        colorRepository.delete(color);
    }

    public void deleteAllColors() {
        colorRepository.deleteAll();
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandByValue(String name) {
        return brandRepository.findByName(name);
    }

    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public void deleteBrand(Brand brand) {
        brandRepository.delete(brand);
    }

    public void deleteAllBrands() {
        brandRepository.deleteAll();
    }

}
