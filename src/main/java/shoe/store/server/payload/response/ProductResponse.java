package shoe.store.server.payload.response;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoe.store.server.models.Product;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private String id;
        private int size;
        private String color;
        private int stock;
    }
    private List<ProductItem> products;

    private String id;

    private boolean productStatus;
    private String productCode;
    private String productName;
    private String productDetail;
    private String shortTitle;
    private String materials;
    private List<String> productPhotos;
    private String brand;
    private String category;
    private double price;
    private double promotion;
    private int weight; // đơn vị gram
    private long createdAt;

    private Set<String> color = new HashSet<>();
    private Set<Integer> size = new HashSet<>();
    private Map<String, List<Integer>> stock = new HashMap<>();

    public void setProductInfo(Product product) {
        this.setId(product.getProductCode());
        this.setProductStatus(product.isProductStatus());
        this.setProductCode(product.getProductCode());
        this.setProductName(product.getProductName());
        this.setProductDetail(product.getProductDetail());
        this.setShortTitle(product.getShortTitle());
        this.setMaterials(product.getMaterials());
        this.setProductPhotos(product.getProductPhotos());
        this.setBrand(product.getBrand());
        this.setCategory(product.getCategory());
        this.setPrice(product.getPrice());
        this.setPromotion(product.getPromotion());
        this.setWeight(product.getWeight());
        this.setCreatedAt(product.getCreatedAt());
    }

    public void setProducts(List<Product> products, String colorFilter) {
        this.products = new ArrayList<>();
        for (Product product : products) {
            if (colorFilter != null && !colorFilter.equals(product.getColor())) continue;
            String color = this.convertColorToCode(product.getColor());
            int size = product.getSize();
            this.color.add(color);
            this.size.add(size);
            if (this.stock.get(color) != null) {
                this.stock.get(color).add(size);
            } else {
                List<Integer> newSize = new ArrayList<>();
                newSize.add(size);
                this.stock.put(color, newSize);
            }

            this.products.add(new ProductItem(product.getId(), product.getSize(), product.getColor(), product.getStock()));
        }
    }

    private String convertColorToCode(String color) {
        if (color == null) return "";
        switch (color) {
            case "": return "";
            case "DE": return "#000000";
            case "TR": return "#FFFFFF";
            case "XL": return "#00FF00";
            case "XB": return "#0000FF";
            case "DO": return "#FF0000";
            case "BE": return "#F5F5DC";
            case "NA": return "#A0522D";
            case "TI": return "#EE82EE";
            case "VA": return "#FFD700";
            case "XA": return "#808080";
            case "CA": return "#FFA500";
            case "HO": return "#FFC0CB";
            default: return "linear-gradient(45deg, orange , yellow, green, cyan, blue, violet)";
        }
    }
}
