package shoe.store.server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "color")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Color {
    @Id
    private String id;

    private String name;
    private String value;
}
