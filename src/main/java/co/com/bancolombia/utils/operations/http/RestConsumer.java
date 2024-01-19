package co.com.bancolombia.utils.operations.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

@UtilityClass
public class RestConsumer {
  static ObjectMapper objectMapper = instantiateMapper();
  static ObjectMapper objectXmlMapper = instantiateXmlMapper();
  static OkHttpClient client = new OkHttpClient();

  public static <T> T getRequest(String url, Class<T> classModel) throws IOException {
    return getRequest(url, classModel, false);
  }

  public static <T> T getRequest(String url, Class<T> classModel, boolean isXml)
      throws IOException {
    Request request = new Request.Builder().url(url).build();

    return getModel(
        Objects.requireNonNull(client.newCall(request).execute().body()), classModel, isXml);
  }

  public static <T> T postRequest(String url, Object body, Class<T> classModel) throws IOException {
    Request request =
        new Request.Builder()
            .url(url)
            .post(RequestBody.create(objectMapper.writeValueAsBytes(body)))
            .build();

    return getModel(Objects.requireNonNull(client.newCall(request).execute().body()), classModel);
  }

  public static void downloadFile(String url, Path out) throws IOException {
    Request request = new Request.Builder().url(url).build();
    Files.write(out, client.newCall(request).execute().body().bytes());
  }

  private static <T> T getModel(ResponseBody response, Class<T> modelClass) throws IOException {
    return getModel(response, modelClass, false);
  }

  private static <T> T getModel(ResponseBody response, Class<T> modelClass, boolean isXml)
      throws IOException {
    final String body = response.string();
    ObjectMapper mapper = isXml ? objectXmlMapper : objectMapper;
    if (!body.isEmpty()) {
      return mapper.readValue(body, modelClass);
    }
    return null;
  }

  private static ObjectMapper instantiateMapper() {
    ObjectMapper mapper = new ObjectMapper();
    customizeMapper(mapper);
    return mapper;
  }

  private static ObjectMapper instantiateXmlMapper() {
    ObjectMapper mapper = new XmlMapper();
    customizeMapper(mapper);
    return mapper;
  }

  private static void customizeMapper(ObjectMapper mapper) {
    mapper.findAndRegisterModules();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
