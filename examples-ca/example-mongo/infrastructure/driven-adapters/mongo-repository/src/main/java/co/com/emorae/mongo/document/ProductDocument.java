package co.com.emorae.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDocument {

    private String id;

    private String name;

    private int quantity;

    private String description;

    private Double price = 0.0;

    private Double discount = 0.0;

    private Set<ToppingDocument> toppings = new HashSet<>();
}
