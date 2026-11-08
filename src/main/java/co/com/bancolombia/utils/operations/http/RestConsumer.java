package co.com.bancolombia.utils.operations.http;

import java.io.IOException;
import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
    return execute(request, classModel, isXml);
  }

  public static <T> T postRequest(String url, Object body, Class<T> classModel) throws IOException {
    Request request =
        new Request.Builder()
            .url(url)
            .post(RequestBody.create(objectMapper.writeValueAsBytes(body)))
            .build();
    return execute(request, classModel, false);
  }

  public static String getTextRequest(String url) throws IOException {
    return executeForBody(new Request.Builder().url(url).build());
  }

  private static <T> T execute(Request request, Class<T> modelClass, boolean isXml)
      throws IOException {
    String body = executeForBody(request);
    if (body.isEmpty()) {
      return null;
    }
    ObjectMapper mapper = isXml ? objectXmlMapper : objectMapper;
    return mapper.readValue(body, modelClass);
  }

  private static String executeForBody(Request request) throws IOException {
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("HTTP " + response.code() + " for " + request.url());
      }
      return response.body().string();
    }
  }

  private static ObjectMapper instantiateMapper() {
    return JsonMapper.builder().findAndAddModules().build();
  }

  private static ObjectMapper instantiateXmlMapper() {
    return XmlMapper.builder().findAndAddModules().build();
  }
}
