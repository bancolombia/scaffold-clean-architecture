package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.customer.Customer;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<Customer, String, co.com.bancolombia.dynamodb.models.Customer> {

    public DynamoDBTemplateAdapter(AmazonDynamoDB connectionFactory, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, Customer.class));
    }
}
