package co.com.bancolombia.utils.operations;

import static co.com.bancolombia.utils.operations.HttpOperations.DEPENDENCY_RELEASES;
import static co.com.bancolombia.utils.operations.HttpOperations.GRADLE_PLUGINS;
import static co.com.bancolombia.utils.operations.HttpOperations.GRADLE_VERSIONS_API;
import static co.com.bancolombia.utils.operations.HttpOperations.PLUGIN_RELEASES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpOperationsTest {

  private final MockWebServer server = new MockWebServer();
  private HttpOperations operations;

  @BeforeEach
  void setup() throws IOException {
    String releaseResponse = "[{\"tag_name\":\"2.0.0\",\"published_at\":\"2021-11-18T13:30:02Z\"}]";
    String gradleVersionResponse = "{\"version\":\"9.4.0\"}";
    String dependencyResponse =
        """
                <metadata>
                  <groupId>some.dependency</groupId>
                  <artifactId>name</artifactId>
                  <versioning>
                    <latest>2.0.1</latest>
                    <release>2.0.1</release>
                    <versions>
                      <version>0.0.1</version>
                    </versions>
                  </versioning>
                </metadata>
                """;
    String xmlResponse =
        """
                <metadata>
                  <groupId>org.sonarqube</groupId>
                  <artifactId>org.sonarqube.gradle.plugin</artifactId>
                  <version>4.4.1.3373</version>
                </metadata>
                """;
    final Dispatcher dispatcher =
        new Dispatcher() {
          @Override
          public @NotNull MockResponse dispatch(RecordedRequest request) {
            return switch (Objects.requireNonNull(request.getPath())) {
              case "/releases" -> new MockResponse().setResponseCode(200).setBody(releaseResponse);
              case "/name/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(dependencyResponse);
              case "/maven-metadata.xml" ->
                  new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(xmlResponse);
              case "/versions/current" ->
                  new MockResponse().setResponseCode(200).setBody(gradleVersionResponse);
              case "/analytics" -> new MockResponse().setResponseCode(201);
              default -> new MockResponse().setResponseCode(404);
            };
          }
        };
    server.setDispatcher(dispatcher);
    server.start();
    operations =
        new HttpOperations(
            Map.of(
                PLUGIN_RELEASES,
                server.url("/releases").toString(),
                DEPENDENCY_RELEASES,
                server.url("name/maven-metadata.xml").toString(),
                GRADLE_PLUGINS,
                server.url("/maven-metadata.xml").toString(),
                GRADLE_VERSIONS_API,
                server.url("/versions/current").toString()));
  }

  @Test
  void getVersionPlugin() {
    // Arrange
    // Act
    Release result = operations.getLatestPluginVersion();
    // Assert
    assertEquals("2.0.0", result.getTagName());
    assertEquals("2021-11-18T13:30:02Z", result.getPublishedAt().toString());
  }

  @Test
  void getGradlePluginVersion() {
    // Arrange
    DependencyRelease release = DependencyRelease.from("org.sonarqube:org.sonarqube.gradle.plugin");
    // Act
    Optional<DependencyRelease> result = operations.getLatestGradlePluginVersion(release);
    // Assert
    assertTrue(result.isPresent());
    assertEquals("4.4.1.3373", result.get().getVersion());
  }

  @Test
  void getDependencyVersion() {
    // Arrange
    DependencyRelease release = DependencyRelease.from("some.dependency:name");
    // Act
    Optional<DependencyRelease> result = operations.getTheLastDependencyRelease(release);
    // Assert
    assertTrue(result.isPresent());
    assertEquals("2.0.1", result.get().getVersion());
  }

  @Test
  void getGradleWrapperVersion() {
    // Arrange
    // Act
    Optional<String> version = operations.getGradleWrapperFromFile();
    // Assert
    assertTrue(version.isPresent());
    assertEquals("9.4.0", version.get());
  }

  @Test
  void sendAnalytics() throws Exception {
    // Arrange
    HttpUrl baseUrl = server.url("/analytics");
    String body = "somebody";
    // Act
    String result = RestConsumer.postRequest(baseUrl.toString(), body, String.class);
    // Assert
    assertNull(result);
  }
}
