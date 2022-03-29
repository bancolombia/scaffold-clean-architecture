package co.com.emorae.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

@Document(collection = "order")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDocument {

    @Id
    private String id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    private String totalProducts;

    private String status;

    private Date paymentDate;

    private Date sentDate;

    private String phone;

    private Double costDelivery = 0.0;

    private String total;

    private Double totalDiscounts = 0.0;

    private Map<String,String> clientInfo = new HashMap<>();

    private Map<String,String> paymentInfo = new HashMap<>();

    private Map<String,String> deliveryInfo = new HashMap<>();

    private Map<String,String> metadata = new HashMap<>();

    private Set<ProductDocument> products = new HashSet<>();

}


