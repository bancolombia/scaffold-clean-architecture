package co.com.bancolombia.usecase.consumeservice;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;


@AllArgsConstructor
public class ConsumeServiceUseCase {

    private UserRepository repository;

    public String sum(Integer x, Integer y) throws IOException {
        return repository.sum(x,y);
    }

    public List<User> getUsers() throws IOException {
        return repository.getUsers();
    }
}
