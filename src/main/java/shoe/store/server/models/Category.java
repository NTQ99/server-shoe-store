package shoe.store.server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SizeRange {
        private int min;
        private int max;
    }
    @Id
    private String id;

    private String categoryName;
    private SizeRange sizeRange;
}
