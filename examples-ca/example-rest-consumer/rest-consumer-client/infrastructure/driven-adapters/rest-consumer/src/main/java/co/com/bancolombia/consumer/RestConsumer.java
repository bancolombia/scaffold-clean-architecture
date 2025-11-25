package co.com.bancolombia.consumer;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Component
public class RestConsumer implements UserRepository {
    private final RestClient restClient;

    public RestConsumer(@Value("${adapter.restconsumer.url}") String url) {
        this.restClient = RestClient.builder().baseUrl(url).build();
    }

    @Override
    public String sum(Integer x, Integer y) throws IOException {
        return restClient
                .get()
                .uri("/sum/{x}/{y}", x, y)
                .retrieve()
                .body(String.class);
    }

    @Override
    public List<User> getUsers() throws IOException {
        return restClient
                .get()
                .uri("/list-users")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}