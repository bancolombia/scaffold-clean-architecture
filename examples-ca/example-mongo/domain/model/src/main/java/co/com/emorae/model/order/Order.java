package co.com.emorae.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private String id;

    private Date createAt;

    private Date paymentDate;

    private Date sentDate;

    private String phone;

    private String totalProducts;

    private OrderStatus status = OrderStatus.SHOPPING_KART;

    private Double costDelivery = 0.0;

    private String total;

    private String totalDiscounts;

    private Map<String,String> clientInfo = new HashMap<>();

    private Map<String,String> paymentInfo = new HashMap<>();

    private Map<String,String> deliveryInfo = new HashMap<>();

    private Map<String,String> metadata = new HashMap<>();

    private Set<Product> products = new HashSet<>();

}
