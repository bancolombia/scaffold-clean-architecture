package co.com.bancolombia.utils.operations.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;

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
    return JsonMapper.builder().findAndAddModules().build();
  }

  private static ObjectMapper instantiateXmlMapper() {
    return XmlMapper.builder().findAndAddModules().build();
  }
}
