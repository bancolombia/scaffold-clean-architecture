package co.com.bancolombia.usecase.consumeservice;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
public class ConsumeServiceUseCase {

    private final UserRepository repository;

    public String sum(Integer x, Integer y) throws IOException {
        return repository.sum(x, y);
    }

    public List<User> getUsers() throws IOException {
        return repository.getUsers();
    }
}
