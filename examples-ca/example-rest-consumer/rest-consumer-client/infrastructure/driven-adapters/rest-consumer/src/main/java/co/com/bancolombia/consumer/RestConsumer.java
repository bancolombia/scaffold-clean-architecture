package co.com.bancolombia.consumer;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RestConsumer implements UserRepository {
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    @Value("${adapter.restconsumer.url}")
    private String url;
    public RestConsumer(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    // these methods are an example that illustrates the implementation of OKHTTP Client.
    // You should use the methods that you implement from the Gateway from the domain.
    @Override
    public String sum(Integer x, Integer y) throws IOException {
        Request request = new Request.Builder()
                .url(url.concat("/sum/").concat(String.valueOf(x)).concat("/").concat(String.valueOf(y)))
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        return mapper.readValue(client.newCall(request).execute().body().string(), String.class);
    }

    @Override
    public List<User> getUsers() throws IOException {

        Request request = new Request.Builder()
                .url(url.concat("/list-users"))
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = null;

        return mapper.readValue(client.newCall(request).execute().body().string(), new TypeReference<>() {
        });

    }

}