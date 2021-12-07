package co.com.bancolombia.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class RestConsumer {
  static ObjectMapper objectMapper = instantiateMapper();
  static OkHttpClient client = new OkHttpClient();

  private RestConsumer() {}

  public static <T> T callRequest(String url, Class<T> classModel) throws Exception {
    Request request = new Request.Builder().url(url).build();

    return getModel(Objects.requireNonNull(client.newCall(request).execute().body()), classModel);
  }

  private static <T> T getModel(ResponseBody response, Class<T> modelClass) throws Exception {
    return objectMapper.readValue(response.string(), modelClass);
  }

  private static ObjectMapper instantiateMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }
}
