package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;

import java.io.IOException;
import java.util.List;

public interface UserRepository {
    String sum(Integer x, Integer y) throws IOException;

    List<User> getUsers() throws IOException;
}
