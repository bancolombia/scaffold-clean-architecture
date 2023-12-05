package co.com.bancolombia.usecase.customer;

import co.com.bancolombia.model.customer.Customer;
import co.com.bancolombia.model.customer.gateways.CustomerRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CustomerUseCase {

    private final CustomerRepository drivenAdapter;

    public List<Customer> findAll() {
        return drivenAdapter.findAll();
    }

    public Customer create(Customer customer) {
        return drivenAdapter.save(customer);
    }

    public Customer getByID(String id) {
        return drivenAdapter.getById(id);
    }

    public void delete(String id) {
        Customer customer = drivenAdapter.getById(id);
        drivenAdapter.delete(customer);
    }
}
