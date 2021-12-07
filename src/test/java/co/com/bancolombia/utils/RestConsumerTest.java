package co.com.bancolombia.utils;

import co.com.bancolombia.models.Release;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestConsumerTest {

  MockWebServer server = new MockWebServer();

  @Before
  public void setUp() throws IOException {
    String response =
        "[\n"
            + "    {\n"
            + "        \"tag_name\": \"2.0.0\",\n"
            + "        \"published_at\": \"2021-11-18T13:30:02Z\"\n"
            + "    }\n"
            + "]";
    server.enqueue(new MockResponse().setBody(response));
    server.start();
  }

  @Test
  public void getVersionPlugin() throws Exception {
    HttpUrl baseUrl = server.url("/releases");

    Release result = RestConsumer.callRequest(baseUrl.toString(), Release[].class)[0];
    Assert.assertEquals("2.0.0", result.getTagName());
    Assert.assertEquals("2021-11-18T13:30:02Z", result.getPublishedAt().toString());
  }
}
