package co.com.bancolombia.utils.operations;

import static co.com.bancolombia.utils.operations.HttpOperations.DEPENDENCY_RELEASES;
import static co.com.bancolombia.utils.operations.HttpOperations.GRADLE_PLUGINS;
import static co.com.bancolombia.utils.operations.HttpOperations.GRADLE_WRAPPER_PROPERTIES;
import static co.com.bancolombia.utils.operations.HttpOperations.PLUGIN_RELEASES;
import static co.com.bancolombia.utils.operations.HttpOperations.SPRING_INITIALIZER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.FileUtilsTest;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpOperationsTest {

  private final MockWebServer server = new MockWebServer();
  private HttpOperations operations;

  @BeforeEach
  public void setUp() throws IOException {
    String releaseResponse = "[{\"tag_name\":\"2.0.0\",\"published_at\":\"2021-11-18T13:30:02Z\"}]";
    String dependencyResponse =
        "{\"response\":{\"docs\":[{\"v\":\"2.0.1\",\"g\":\"some.dependency\",\"a\":\"name\"}]}}";
    String xmlResponse =
        "<metadata><groupId>org.sonarqube</groupId><artifactId>org.sonarqube.gradle.plugin</artifactId><version>4.4.1.3373</version></metadata>";
    final Dispatcher dispatcher =
        new Dispatcher() {
          @Override
          public @NotNull MockResponse dispatch(RecordedRequest request) {
            switch (Objects.requireNonNull(request.getPath())) {
              case "/releases":
                {
                  return new MockResponse().setResponseCode(200).setBody(releaseResponse);
                }
              case "/maven":
                {
                  return new MockResponse().setResponseCode(200).setBody(dependencyResponse);
                }
              case "/maven-metadata.xml":
                {
                  return new MockResponse()
                      .setResponseCode(200)
                      .addHeader("Content-Type", "application/xml")
                      .setBody(xmlResponse);
                }
              case "/analytics":
                {
                  return new MockResponse().setResponseCode(201);
                }
              case "/demo.zip":
                {
                  Path path = Paths.get("build", "test.zip");
                  try {
                    Buffer buffer = new Buffer();
                    buffer.write(Files.readAllBytes(path));
                    return new MockResponse()
                        .setBody(buffer)
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/zip");
                  } catch (IOException e) {
                    return new MockResponse().setResponseCode(500);
                  }
                }
            }
            return new MockResponse().setResponseCode(404);
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
                server.url("/maven").toString(),
                GRADLE_PLUGINS,
                server.url("/maven-metadata.xml").toString(),
                SPRING_INITIALIZER,
                server.url("/demo.zip").toString()));
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
  void getGradleWrapperVersion() throws Exception {
    // Arrange
    Files.createDirectories(Path.of("build/gradle/wrapper"));
    String zipFilePath = "build/test.zip";
    String textContent =
        "distributionUrl=https\\://services.gradle.org/distributions/gradle-8.5.1-bin.zip";
    Path tempFilePath =
        FileUtilsTest.createTempTextFile("build/" + GRADLE_WRAPPER_PROPERTIES, textContent);
    FileUtilsTest.createZipFile(zipFilePath, tempFilePath, GRADLE_WRAPPER_PROPERTIES);
    Path out = Path.of("build", "demo.zip");
    // Act
    Optional<String> version = operations.getGradleWrapperFromFile();
    // Assert
    assertTrue(Files.exists(out));
    assertTrue(version.isPresent());
    assertEquals("8.5.1", version.get());
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
