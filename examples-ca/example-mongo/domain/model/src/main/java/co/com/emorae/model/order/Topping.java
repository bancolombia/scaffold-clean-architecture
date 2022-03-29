package co.com.emorae.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topping {

    private String id;

    private String name;

    private Integer quantity = 0;

    private Double price = 0.0;

    private String description;
}
