package shoe.store.server.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import shoe.store.server.models.Product;
import shoe.store.server.models.ProductGroup;
import shoe.store.server.models.Size;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.payload.request.QueryRequest;
import shoe.store.server.payload.response.ProductResponse;
import shoe.store.server.models.Color;
import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.Brand;
import shoe.store.server.models.Category;
import shoe.store.server.repositories.BrandRepository;
import shoe.store.server.repositories.CategoryRepository;
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

    @Autowired
    private CategoryRepository categoryRepository;

    public Product saveProduct(Product product) {

        Brand brand = new Brand();
        brand.setName(product.getBrand());
        this.saveBrand(brand);
        return productRepository.save(product);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<ProductResponse> groupByProductCode(QueryRequest queryRequest) {
        List<ProductGroup> productGroups = productRepository.findProductsGroupByProductCode();
        List<ProductResponse> responses = new ArrayList<>();
        for (ProductGroup productGroup : productGroups) {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setProductInfo(productGroup.getProducts().get(0));
            if (queryRequest == null) queryRequest = new QueryRequest();
            try {
                if (queryRequest.getSearch() != null) {
                    String search = java.net.URLDecoder.decode(queryRequest.getSearch(), "UTF-8");
                    if (productResponse.getProductName() == null || !productResponse.getProductName().toUpperCase().contains(search.toUpperCase())) continue;
                }
                if (queryRequest.getBrand() != null) {
                    String brand = java.net.URLDecoder.decode(queryRequest.getBrand(), "UTF-8");
                    if (productResponse.getBrand() == null || !productResponse.getBrand().toUpperCase().contains(brand.toUpperCase())) continue;
                }
                if (queryRequest.getType() != null) {
                    String category = java.net.URLDecoder.decode(queryRequest.getType(), "UTF-8");
                    if (productResponse.getCategory() == null || !productResponse.getCategory().toUpperCase().contains(category.toUpperCase())) continue;
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            productResponse.setProducts(productGroup.getProducts(), queryRequest.getColor());
            if (!productResponse.getProducts().isEmpty()) responses.add(productResponse);
        }

        return responses;
    }

    public Product getProductByCode(String code) {
        return productRepository.findByProductCode(code);
    }

    public List<Product> getProductsByCode(String code) {
        return productRepository.findByProductCodeContaining(code);
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

    public Product updateProduct(String id, Product newProductData) {

        Product productData = this.getProductById(id);

        if (productData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }

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

    public Size saveSize(Size size) {
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

    public String getColorCode(String id) {
        Color color = colorRepository.findById(id).orElse(null);
        if (color == null) return "";
        else return color.getValue();
    }

    public Color saveColor(Color color) {
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

    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public void deleteBrand(Brand brand) {
        brandRepository.delete(brand);
    }

    public void deleteAllBrands() {
        brandRepository.deleteAll();
    }

    public List<Category> getAllCates() {
        return categoryRepository.findAll();
    }

    public Category getCateByName(String name) {
        return categoryRepository.findByCategoryName(name);
    }

    public Category saveCate(Category cate) {
        return categoryRepository.save(cate);
    }

    public void deleteCate(Category cate) {
        categoryRepository.delete(cate);
    }

    public void deleteAllCates() {
        categoryRepository.deleteAll();
    }

    public Map<?,?> getNumOfProductByCategory() {
        Map<String, Integer> res = new HashMap<>();
        List<Category> cateList = this.getAllCates();
        for (Category cate : cateList) {
            res.put(cate.getCategoryName(), productRepository.countByCategory(cate.getCategoryName()));
        }

        return res;
    }

    public Map<?,?> getNumOfProductByBrand() {
        Map<String, Integer> res = new HashMap<>();
        List<Brand> brandList = this.getAllBrands();
        for (Brand brand : brandList) {
            res.put(brand.getName(), productRepository.countByBrand(brand.getName()));
        }

        return res;
    }

    public Map<?,?> getNumOfProductByColor() {
        Map<String, Integer> res = new HashMap<>();
        List<Color> colorList = this.getAllColors();
        for (Color color : colorList) {
            res.put(color.getId(), productRepository.countByColor(color.getId()));
        }
        
        return res;
    }

}
