package co.com.bancolombia.dynamodb.gateway;

import co.com.bancolombia.dynamodb.DynamoDBTemplateAdapter;
import co.com.bancolombia.model.customer.Customer;
import co.com.bancolombia.model.customer.gateways.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CustomerGatewayImpl implements CustomerRepository {

    private final DynamoDBTemplateAdapter repository;

    @Override
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public Customer getById(String id) {
        return repository.getById(id);
    }

    @Override
    public void delete(Customer customer) {
        repository.delete(customer);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }
}
