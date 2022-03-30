package co.com.bancolombia.model.customer.gateways;

import co.com.bancolombia.model.customer.Customer;

import java.util.List;

public interface CustomerRepository {

    Customer save(Customer customer);

    Customer getById(String id);

    void delete(Customer customer);

    List<Customer> findAll();

}
