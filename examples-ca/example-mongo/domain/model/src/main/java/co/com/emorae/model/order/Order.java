package co.com.emorae.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @Builder.Default
    private OrderStatus status = OrderStatus.SHOPPING_KART;

    @Builder.Default
    private Double costDelivery = 0.0;

    private String total;

    private String totalDiscounts;

    @Builder.Default
    private Map<String, String> clientInfo = new HashMap<>();

    @Builder.Default
    private Map<String, String> paymentInfo = new HashMap<>();

    @Builder.Default
    private Map<String, String> deliveryInfo = new HashMap<>();

    @Builder.Default
    private Map<String, String> metadata = new HashMap<>();

    @Builder.Default
    private Set<Product> products = new HashSet<>();

}
