package co.com.bancolombia.consumer;

import co.com.bancolombia.model.usuario.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestConsumer // implements Gateway from domain
{

    @Value("${adapter.restconsumer.url}")
    private String url;
    private final OkHttpClient client;
    private final ObjectMapper mapper;


    // these methods are an example that illustrates the implementation of OKHTTP Client.
    // You should use the methods that you implement from the Gateway from the domain.

    public Integer getSum(Integer x, Integer z) throws IOException {

        Request request = new Request.Builder()
            .url(url.concat("/sum/").concat(String.valueOf(x)).concat("/").concat(String.valueOf(z)))
            .get()
            .addHeader("Content-Type","application/json")
            .build();

        return mapper.readValue(client.newCall(request).execute().body().string(), Integer.class);
    }

    public List<Usuario> getUsers() throws IOException {

        Request request = new Request.Builder()
                .url(url.concat("/users"))
                .get()
                .addHeader("Content-Type","application/json")
                .build();

        return mapper.readValue(client.newCall(request).execute().body().string(), List.class);
    }



}