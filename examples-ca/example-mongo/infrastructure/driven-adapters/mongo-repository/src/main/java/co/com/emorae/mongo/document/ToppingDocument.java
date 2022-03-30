package co.com.emorae.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToppingDocument {

    private String id;

    private String name;

    private Integer quantity = 0;

    private Double price = 0.0;

    private String description;
}
