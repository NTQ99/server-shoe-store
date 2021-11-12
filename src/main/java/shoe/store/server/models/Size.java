package shoe.store.server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "size")
@Data @NoArgsConstructor @AllArgsConstructor
public class Size {
    @Id
    private String id;

    private int value;
}
