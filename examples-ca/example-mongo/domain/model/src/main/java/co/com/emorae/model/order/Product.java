package co.com.emorae.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private String id;

    private String name;

    private Integer quantity = 0;

    private String description;

    private Double price = 0.0;

    private Double discount = 0.0;

    private Set<Topping> toppings = new HashSet<>();

}
