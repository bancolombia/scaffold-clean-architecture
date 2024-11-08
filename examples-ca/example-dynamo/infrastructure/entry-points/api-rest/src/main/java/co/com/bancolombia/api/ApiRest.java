package co.com.bancolombia.api;

import co.com.bancolombia.model.customer.Customer;
import co.com.bancolombia.usecase.customer.CustomerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {

    private final CustomerUseCase useCase;

    @GetMapping(path = "/customer/{id}")
    public Customer getById(@PathVariable String id) {
        return useCase.getByID(id);
    }

    @GetMapping(path = "/customer")
    public List<Customer> findAll() {
        return useCase.findAll();
    }

    @PostMapping(path = "/customer")
    public Customer save(@RequestBody Customer customer) {
        return useCase.create(customer);
    }

    @DeleteMapping(path = "/customer/{id}")
    public void delete(@PathVariable String id) {
        useCase.delete(id);
    }
}
