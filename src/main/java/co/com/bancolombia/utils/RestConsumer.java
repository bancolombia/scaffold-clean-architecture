package co.com.bancolombia.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class RestConsumer {
  static ObjectMapper objectMapper = instantiateMapper();
  static OkHttpClient client = new OkHttpClient();

  private RestConsumer() {}

  public static <T> T callRequest(String url, Class<T> classModel) throws IOException {
    Request request = new Request.Builder().url(url).build();

    return getModel(Objects.requireNonNull(client.newCall(request).execute().body()), classModel);
  }

  public static <T> T postRequest(String url, Object body, Class<T> classModel) throws IOException {
    Request request =
        new Request.Builder()
            .url(url)
            .post(RequestBody.create(objectMapper.writeValueAsBytes(body)))
            .build();

    return getModel(Objects.requireNonNull(client.newCall(request).execute().body()), classModel);
  }

  private static <T> T getModel(ResponseBody response, Class<T> modelClass) throws IOException {
    final String body = response.string();
    if (!body.isEmpty()) {
      return objectMapper.readValue(body, modelClass);
    }
    return null;
  }

  private static ObjectMapper instantiateMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }
}
