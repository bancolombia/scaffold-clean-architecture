package co.com.bancolombia.utils.operations.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.models.MavenMetadata;
import co.com.bancolombia.models.Release;
import java.io.IOException;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestConsumerTest {
  private MockWebServer server;

  @BeforeEach
  void setup() throws IOException {
    server = new MockWebServer();
    server.start();
  }

  @AfterEach
  void tearDown() throws IOException {
    server.close();
  }

  private String url() {
    return server.url("/resource").toString();
  }

  @Test
  void shouldThrowWhenServerReturnsNotFoundHtml() {
    server.enqueue(
        new MockResponse().setResponseCode(404).setBody("<html><body>Not Found</body></html>"));

    IOException error =
        assertThrows(
            IOException.class, () -> RestConsumer.getRequest(url(), MavenMetadata.class, true));

    assertTrue(error.getMessage().startsWith("HTTP 404 for "));
  }

  @Test
  void shouldThrowWhenServerReturnsRateLimit() {
    server.enqueue(
        new MockResponse()
            .setResponseCode(403)
            .setBody("{\"message\":\"API rate limit exceeded\"}"));

    IOException error =
        assertThrows(IOException.class, () -> RestConsumer.getRequest(url(), Release[].class));

    assertTrue(error.getMessage().startsWith("HTTP 403 for "));
  }

  @Test
  void shouldThrowWhenServerErrorHasEmptyBody() {
    server.enqueue(new MockResponse().setResponseCode(500));

    assertThrows(IOException.class, () -> RestConsumer.getRequest(url(), Release[].class));
  }

  @Test
  void shouldThrowWhenPostIsNotSuccessful() {
    server.enqueue(new MockResponse().setResponseCode(400));

    assertThrows(
        IOException.class, () -> RestConsumer.postRequest(url(), Map.of("a", 1), Map.class));
  }

  @Test
  void shouldParseSuccessfulResponse() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("[{\"tag_name\":\"5.1.0\"}]"));

    Release[] releases = RestConsumer.getRequest(url(), Release[].class);

    assertEquals("5.1.0", releases[0].getTagName());
  }

  @Test
  void shouldReturnNullWhenSuccessfulResponseHasNoBody() throws IOException {
    server.enqueue(new MockResponse().setResponseCode(201));

    assertNull(RestConsumer.postRequest(url(), Map.of("a", 1), Map.class));
  }
}
